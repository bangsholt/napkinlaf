UI.single.list	:=  ButtonUI ColorChooserUI ComboBoxUI DesktopIconUI \
		    DesktopPaneUI EditorPaneUI LabelUI ListUI MenuBarUI \
		    OptionPaneUI PanelUI PopupMenuUI ProgressBarUI RootPaneUI \
		    ScrollBarUI ScrollPaneUI SeparatorUI SpinnerUI SplitPaneUI \
		    TabbedPaneUI TableHeaderUI TextAreaUI TextFieldUI TableUI \
		    ToolBarUI ToolTipUI TreeUI ViewportUI

UI.multi.list	:=  FileChooserUI InternalFrameUI SliderUI

UI.list		:= $(UI.single.list) $(UI.multi.list)

FILES.add	+= $(UI.list:%=./Napkin%.java) NapkinGenerated.java

$(UI.single.list:%=./Napkin%.java): UISingleton.tmpl
	-rm -f $@
	pat=`echo $* | sed -e s/Napkin// -e s/UI//` ; sed -e "s/@(component)@/$$pat/g" $< > $@

$(UI.multi.list:%=./Napkin%.java): UIMultiple.tmpl
	-rm -f $@
	pat=`echo $* | sed -e s/Napkin// -e s/UI//` ; sed -e "s/@(component)@/$$pat/g" $< > $@

NapkinGenerated.java: GNUmakefile.inc
	-rm -f $@
	(echo "package napkin;" ; \
	 echo "interface NapkinGenerated {"; \
	 echo "    String[] UI_TYPES = {"; \
	 for f in $(UI.list); do echo "        \"$$f\","; done; \
	 echo "    };" ; echo "}") > $@
