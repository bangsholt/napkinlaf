# ----------------------------------------------------------------------------
# Functions
# ----------------------------------------------------------------------------
# (can't use := for functions

SEARCH		 = $(sort $(shell find $(1) $(2) -print))

# ----------------------------------------------------------------------------
# External packages
# ----------------------------------------------------------------------------

ifndef JAVA.bin
    JAVA.cmd	:= $(shell which javac)
    JAVA.bin	:= $(dir $(JAVA.cmd))
endif

JAVA.dir	:= $(JAVA.bin)/..
JAVA		:= $(JAVA.bin)/java
JAVAC		:= $(JAVA.bin)/javac
JAVADOC		:= $(JAVA.bin)/javadoc
RMIC		:= $(JAVA.bin)/rmic

# ----------------------------------------------------------------------------
# Variable definitions
# ----------------------------------------------------------------------------

PWD		:= $(shell pwd)
TOP		:= $(shell (echo $(PWD)) | sed -e 's,/src.*,,')
SRC		:= $(TOP)/src
DOC		:= $(TOP)/doc
CLASSDIR	:= $(TOP)/classes
APIDOC		:= $(DOC)/api

# Treat top of the source tree specially to make the form of the variable
# constant (otherwise the substitution expression for THISDIR would yield
# PWD instead of PWD with SRC stripped off, and (although harmless)
# CLASSDIR.this would end in "/.").
ifeq ($(PWD),$(SRC))
THISDIR		:= .
CLASSDIR.this	:= $(CLASSDIR)
PKG		:= 
else
THISDIR		:= $(PWD:$(SRC)/%=%)
CLASSDIR.this	:= $(CLASSDIR)/$(THISDIR)
PKG		:= $(subst /,.,$(THISDIR)).
endif

# build list of things to rmic
SERVER.classes	:= # initial blank list
SERVER.paths	 = $(subst .,/,$(sort $(SERVER.classes)))
SERVER.class	 = $(SERVER.paths:%=$(CLASSDIR)/%.class)
SERVER.stubs	 = $(filter $(CLASSDIR.this)/%,$(SERVER.paths:%=$(CLASSDIR)/%_Stub.class))

FIELS.add	 = #initial blank list
FILES.raw	 = $(call SEARCH,.,-name '*.java') $(FILES.add)
FILES.java	 = $(FILES.raw:./%=%)
FILES.class	 = $(FILES.java:%.java=$(CLASSDIR.this)/%.class)

THIS.java	:= $(wildcard *.java)
THIS.class	:= $(THIS.java:%.java=$(CLASSDIR.this)/%.class)
THIS.stubs	:= $(patsubst /$(CLASSDIR.this)/%,,$(SERVER.stubs))

RESOURCES.dirs	:= $(call SEARCH,.,-type d -name resources)
RESOURCES.glob	:= $(addsuffix /*,$(RESOURCES.dirs))
RESOURCES.files	:= $(filer-out /RCS,$(wildcard $(RESOURCES.glob)))
RESOURCES.need	:= $(addprefix $(CLASSDIR.this)/,$(RESOURCES.files))

PACKAGES.raw	:= $(subst /,.,$(sort $(dir $(FILES.java))))
PACKAGES.list	:= $(addprefix $(PKG),$(PACKAGES.raw))
PACKAGES	:= $(PACKAGES.list:%...=%)
PACKAGES	:= $(PACKAGES:%.=%)
PACKAGES.doc	= $(sort $(strip $(filter-out $(NO_DOC),$(PACKAGES))))

# The file that will hold the list of classes that need rebuilding
CLASS_LIST	:= /tmp/.class$(shell echo $$$$)
STUBS_LIST	:= $(subst class,stubs,$(CLASS_LIST))

# How to compile things
JAVAC.echo	= echo javac $(JAVACFLAGS)
JAVAC.exec	= $(JAVAC) -d $(CLASSDIR) -classpath $(CLASSPATH) $(JAVACFLAGS)

JAVAVERSION.exec= $(JAVA) -version

RMIC.echo	= echo rmic $(RMICFLAGS)
RMIC.exec	= $(RMIC) -d $(CLASSDIR) -classpath $(CLASSPATH) $(RMICFLAGS) \
		   -sourcepath $(SOURCEPATH)

# Compilation flags
JAVADEBUG	:=
JAVACFLAGS	:= $(JAVADEBUG) -deprecation
RMICFLAGS	= -v1.2
JAVADOCLEVEL	=
JAVADOCFLAGS	= -version $(JAVADOCLEVEL)

# Start out with the classpath and class paths to include our own stuff
# (these may be added to in subdirectories)
CLASSPATH.dirs	:= $(CLASSDIR)
SOURCEPATH.dirs	:= $(SRC)

# Build the paths from component directories (which requires some very fancy
# gmake magic to get the makefile to notice an actual space as something
# besides a separator).
EMPTY		:=
SPACE		:= $(EMPTY) $(EMPTY)
CLASSPATH	 = $(subst $(SPACE),:,$(CLASSPATH.dirs))
SOURCEPATH	 = $(subst $(SPACE),:,$(SOURCEPATH.dirs))

# ----------------------------------------------------------------------------
# Implicit rule definitions
# ----------------------------------------------------------------------------

.SUFFIXES:
.SUFFIXES: .class .java .zip

# Each class file depends on its corresponding source file
# We add the name of the class that needs building to a file, which is then
# read in the "classes" build rule.
$(CLASSDIR.this)/%.class: %.java
	@echo '$?' >> $(CLASS_LIST)

# stubs and skeletons -- we must get impl class name from dependency
# class Note: we need to check the result of subst is non-null because
# sometimes (usually when the recursive make has already built the
# stubs) this rule will run even when $? evaluates to null.  Without
# the guard this would cause us to add a newline to STUBS_LIST which
# would cause problems when we run rmic.
$(CLASSDIR.this)/%_Stub.class: $(CLASSDIR.this)/%.class
	@set -e ;					      \
	classname='$(subst /,.,$(?:$(CLASSDIR)/%.class=%))' ; \
	if [ -n "$$classname" ]; then                         \
		echo $$classname >> $(STUBS_LIST) ;           \
	fi

# used for putting arbitrary files into the class tree; typically used for
# resources.
$(CLASSDIR.this)/%: %
	@test -d $(dir $@) || mkdir -p $(dir $@)
	cp $? $@

# ----------------------------------------------------------------------------
# Default target
# ----------------------------------------------------------------------------

all: classes stubs

# ----------------------------------------------------------------------------
# Including child makefiles
# ----------------------------------------------------------------------------

INCLUDES	:= $(call SEARCH,.,-name GNUmakefile.inc) \
		   $(call SEARCH,$(SRC),-name GNUmakefile.allinc)

ifdef INCLUDES
    include $(INCLUDES)
endif

vars:
	@echo FILES.raw $(FILES.raw)

# ----------------------------------------------------------------------------
# Targets
# ----------------------------------------------------------------------------

# The class files for this package and all subpackages
classes: $(CLASSDIR) init_java_list $(FILES.class) build_java_list resources

$(CLASSDIR):
	mkdir -p $@

.PHONY: classes init_java_list build_java_list resources

init_java_list:
	@-rm -f $(CLASS_LIST)

# The recursion in this rule is because gmake is not always building all the
# necessary stubs if the stubs are built in the same run as the class that
# creates it.  This is odd, because when run with -d, gmake does the right
# thing.  This bug may be related to the one descirbed for the .DEFAULT rule

build_java_list:
	@set -e ;							\
	 if [ -f $(CLASS_LIST) ]; then					\
	    $(JAVAVERSION.exec) ;					\
	    files=`cat $(CLASS_LIST)` ;					\
	    num=`echo $$files | wc | awk '{ print $$2 }'` ;		\
	    $(JAVAC.echo) "[$$num files]:" ; 				\
	    sc=`expr $${COLUMNS:-76} - 4` ; 				\
	    COLUMNS=$$sc ls -C $$files | expand | sed -e 's/^/    /' ;	\
	    $(JAVAC.exec) $$files ; 					\
	    $(MAKE) --no-print-directory stubs ;			\
	    rm -f $(CLASS_LIST) ;					\
	 fi

# The stubs and skeletons for this package and all subpackages
stubs: init_stub_list $(SERVER.stubs) build_stub_list

.PHONY: classes init_stub_list build_stub_list

# Otherwise gmake thinks that these were only built to get at the stubs,
# and are therefore removable intermediate files (!)
.PRECIOUS: $(SERVER.classes)

init_stub_list:
	@-rm -f $(STUBS_LIST)

build_stub_list:
	@set -e ;							\
	 if [ -s $(STUBS_LIST) ]; then					\
	    $(RMIC.echo) "[Building stub files]:" ;			\
	    sort $(STUBS_LIST) | sed -e 's/^/    /' ;			\
	    $(RMIC.exec) `cat $(STUBS_LIST)`;				\
	    rm -f $(STUBS_LIST) ;					\
	 fi

resources: $(RESOURCES.need)

doc: $(APIDOC)/index.html
.PHONY: doc

$(APIDOC)/index.html: $(FILES.java) $(MAKEFILE)
	@rm -fr $(APIDOC)
	@mkdir -p $(APIDOC)
	@echo javadoc $(JAVADOCFLAGS) -d $(APIDOC) \\
	@echo $(PACKAGES.doc) | awk '{ for (i = 1; i <= NF; i++) print "   ", $$i; }'
	@cd $(SRC) ; $(JAVADOC) $(JAVADOCFLAGS)              \
           -classpath $(CLASSPATH) -sourcepath $(SOURCEPATH) \
           -d $(APIDOC) $(PACKAGES.doc)


# Print out the classpath we are using to build against
#
classpath:
	@echo $(CLASSPATH)

#
clean clobber clean.all clobber.all::
	-rm -rf $(CLASSDIR.this)/ core

clean.jars::
	-rm -rf $(JARSDIR)/

clean.doc clobber clobber.all::
	-rm -rf $(APIDOC)

.PHONY: clean clobber clean.all clobber.all

# Clean all class files for this package
#
clean.this clobber.this::
	-rm -f $(CLASSDIR.this)/*.class core

.PHONY: clean.this clobber.this
