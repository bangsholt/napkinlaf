
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Send data to a URL via the HTTP POST METHOD.
 * <P>
 * This class is not designed for synchronized access.  If you need to use
 * the same object from multiple threads you are advised to synchronize your
 * access to the object or expect unexplainable behavior.
 * <p>
 * Deficiencies:<UL>
 * <LI>The Multipart/MIME boundary string is generated
 * randomly. Therefore the generated POST form data may be
 * unparseable by the server/script every trillionth request ;).
 * However, the server will
 * hopefully not send a "HTTP/1.x 200 OK" status or the processing
 * script will give back an error message.
 * <LI>All data in mime bodies is binary.
 * </UL>
 */
public class PostMethod {

    private static final boolean debug = false;

    /* this vector consists of MimePartTuple mime parts */
    private List mimeParts = new ArrayList();

    /* the HTTP target */
    private URL url = null;

    /* expected answer (if any), otherwise the answer may be investigated
       using the 'getAnswer()' method */
    private String expectedAnswer = null;

    /* did server/script receive this POST successfully? */
    private boolean success = false;

    /* user agent string to put into the POST form header, null: omit */
    private String userAgent = null;

    private char[] mimeBoundary = null;
    private char[] postHeader = null;
    private char[] postBody = null;

    /* server output */
    private String replyHeader = null;
    private char[] replyBody = null;
    private String replyStatus = null; // first line of 'replyHeader'

    /* for reading response line-by-line */
    private BufferedReader reader = null;	// reader for body (if asked)
    private int currentLine;			// current line number in reader

    /**
     * Creates a new <CODE>PostMethod</CODE> object without an initial URL.
     * This object cannot be used to POST until a call to <CODE>setURL</CODE>.
     *
     * @see #setURL
     */
    public PostMethod() {
    }

    /**
     * Creates a new <CODE>PostMethod</CODE> object.
     *
     * @param url The initial URL.
     */
    public PostMethod(URL url) {
	setURL(url);
    }

    /**
     * Sends this POST action to the server/script at the current URL.  This
     * uses the URL to locate the destination, sends the POST header and body
     * information, then receives the results, and returns <CODE>true</CODE>
     * if the POST was successful.  "Successful" means: (a) there were no
     * exceptions during the communication; (b) the result is well formed,: (c)
     * the result header says that it was "okay"; and (d) if there is an
     * expected reply, that the result is that reply.
     * <P>
     * This method may be called as often as you like.
     *
     * @return <CODE>true</CODE> if the POST was successful.
     *
     * @throws IOException An I/O error ocurred while using the URL.
     * @throws NullPointerException No URL has been set.
     *
     * @see #setExpectedAnswer
     */
    public boolean execute() throws IOException {
	success = false;	// reset (in case this is a second execute)

	if (url == null) {
	    throw new NullPointerException("URL not set");
	}

	createMimeBoundary();
	createPostBody();
	createPostHeader();

	Socket socket = new Socket(url.getHost(), getPort());
//			socket.setKeepAlive(false);

	OutputStream o = socket.getOutputStream();
	BufferedWriter w = new BufferedWriter(new OutputStreamWriter(o));
	w.write(postHeader);
	w.write(postBody);
	w.flush();
//			socket.shutdownOutput();
	parseServerReply(socket);
	if (expectedAnswer == null) {
	    success = basicReplyCheck();
	} else {
	    success = checkAnswer();
	}
	return success;
    }

    /**
     * Fills in 'replyheader', 'replyBody', 'replyStatus'
     */
    private void parseServerReply(Socket socket) throws IOException {
	StringBuffer header = new StringBuffer();
	InputStream i = socket.getInputStream();
	BufferedReader r = new BufferedReader(new InputStreamReader(i));
	String s;
	int len = -1;
	while ((s = r.readLine()) != null && !s.equals("")) {
	    if (debug) {
		System.out.println(s);
	    }
	    if (replyStatus == null) {
		replyStatus = s.toString();
	    }
	    header.append(s + "\r\n");
	    if (s.substring(0, 15).toLowerCase().equals("content-length:")) {
		len = Integer.parseInt(s.substring(16));
	    }
	}
	replyHeader = header.toString();

	if (len >= 0) {
	    replyBody = new char[len];
	    int torcv = len;
	    int rcvd = 0;
	    do {
		len = r.read(replyBody, rcvd, torcv);
		torcv -= len;
		rcvd += len;
	    } while (torcv > 0);
	} else {
	    CharArrayWriter w = new CharArrayWriter();
	    char[] buffer = new char[1024];
	    while ((len = r.read(buffer, 0, 1024)) != -1) {
		w.write(buffer, 0, len);
	    }
	    replyBody = w.toCharArray();
	    w.close();
	}

	if (debug) {
	    BufferedWriter wr = new BufferedWriter(new FileWriter(new File("debug.replybody")));
	    wr.write(replyBody);
	    wr.flush();
	    wr.close();
	}

	socket.close();
    }

    private boolean checkAnswer() {
	if (!basicReplyCheck()) {
	    return false;
	}
	return getReplyLine(1).equals(expectedAnswer);
    }

    private boolean basicReplyCheck() {
	boolean wellFormed = false;
	if (replyBody == null) {
	    return wellFormed;
	}
	if (replyHeader == null) {
	    return wellFormed;
	}
	if (replyStatus.toLowerCase().endsWith("200 ok")) {
	    wellFormed = true;
	}
	return wellFormed;
    }

    /**
     * Returns the <i>n</i><SUP>th</SUP> line of the server's reply body.  If
     * the line number is beyond the end of the input, returns
     * <CODE>null</CODE>.
     *
     * @param lineNum The number of the line to return.  Line numbers start
     * 		with one.
     *
     * @return The <i>n</i><SUP>th</SUP> line of the server's reply body.
     */
    public String getReplyLine(int lineNum) {
	if (lineNum <= 0)
	    throw new IllegalArgumentException("line number must be >= 1");
	try {
	    if (reader == null) {
		reader = new BufferedReader(new CharArrayReader(replyBody));
		currentLine = 1;
	    } else if (lineNum < currentLine) {
		reader.reset();
		currentLine = 1;
	    }
	    String s;
	    while ((s = reader.readLine()) != null && currentLine < lineNum) {
		currentLine++;
	    }
	    return s;
	} catch (IOException e) {
	    throw new IllegalStateException("error while reading char array?");
	}
    }

    /**
     * Was this POST action successful?  Always returns the same value as
     * the most recent call to <CODE>execute</CODE>, and if there has been no
     * such call, returns <CODE>false</CODE>.
     *
     * @return <CODDE>true</CODDE> if the last execution was successful.
     *
     * @see #execute
     */
    public boolean wasSuccessful() {
	return success;
    }

    /**
     * Equivalent to
     * <CODE>addMimePart(designator, data.toCharArray())</CODE>.
     *
     * @param designator The designator for the mime part.
     * @param data The source for data for the mime part.
     *
     * @see #addMimePart(String,char[])
     */
    public void addMimePart(String designator, String data) {
	mimeParts.add(new MimePartTuple(designator, data));
    }

    /**
     * Adds a designator/data pair to the body that will be sent with the
     * POST as a mime part.  The designator will be the content disposition's
     * <CODE>name</CODE> value (and if the data is binary, also the
     * <CODE>filename</CODE> value).  The data will be the data bode of the
     * mime part.  For example, the designator <CODE>"SHOUT"</CODE> with a
     * data of <CODE>"marco"</CODE> would result in the following mime part:
     * <PRE>
     * Content-Disposition: form-data; name="SHOUT"; filename="SHOUT"
     * Content-Type: application/octet-stream
     * Content-Transfer-Encoding: base64
     *
     * marco
     * </PRE>
     *
     * @param designator The designator for the mime part
     * @param data The data for the mime part
     */
    public void addMimePart(String designator, char[] data) {
	mimeParts.add(new MimePartTuple(designator, data));
    }

    /**
     * Sets the target URL for future POST calls.
     *
     * @param url The URL to use for future POST calls.
     *
     * @see #execute
     */
    public void setURL(URL url) {
	this.url = url;
    }

    /**
     * Returns the target URL for future POST calls.
     * @return The target URL for future POST calls.
     */
    public URL getURL() {
	return url;
    }

    /**
     * Retrieve the port number to use from the given URL.  If the port is
     * fixed by the URL (if <CODE>url.getPort()</CODE> returns a port number)
     * that number will be used.  Otherwise the protocol will be examined to
     * guess the default port (80 for http; 21 for ftp; 443 for https).
     *
     * @param url The URL to examine for a port.
     *
     * @return The port number derived from the URL, or -1 if none can be.
     */
    public static int getPort(URL url) {
	int port = url.getPort();
	String proto = url.getProtocol().toLowerCase();
	if (port < 0) {
	    if (proto.equals("http")) {
		port = 80;
	    } else if (proto.equals("ftp")) {
		port = 21;
	    } else if (proto.equals("https")) {
		port = 443;
	    }
	}
	return port;
    }

    /**
     * Returns the port derived from the current URL using
     * <CODE>getPort(URL)</CODE>.
     *
     * @return the The port derived from the current URL using
     * <CODE>getPort(URL)</CODE>.
     */
    public int getPort() {
	return getPort(url);
    }

    /**
     * Sets the expected answer: if the server doesn't return exactly this
     * string as the first line  within the server's HTTP reply body,
     * this POST action will be considered unsuccessful.  No end-of-line
     * should be in this answer argument.  A <CODE>null</CODE> value indicates
     * that no check will be done.
     *
     * @param answer The answer that will be expected.
     */
    public void setExpectedAnswer(String answer) {
	if (answer != null &&
		(answer.indexOf('\n') > 0 || answer.indexOf('\r') > 0))
	    throw new IllegalArgumentException("no EOL allowed in answer");
	expectedAnswer = answer;
    }

    /**
     * Returns the body of the most recent reply to a POST.
     * @return The body of the most recent reply to a POST.
     *
     * @throws IllegalStateException Invoked before any POST.
     *
     * @see #execute
     */
    public char[] getReplyBody() {
	if (replyHeader == null)
	    throw new IllegalStateException("no POST yet");
	return replyBody;
    }

    /**
     * Returns the header of the most recent reply to a POST.
     * @return The header of the most recent reply to a POST.
     *
     * @throws IllegalStateException Invoked before any POST.
     *
     * @see #execute
     */
    public String getReplyHeader() {
	if (replyHeader == null)
	    throw new IllegalStateException("no POST yet");
	return replyHeader;
    }

    /**
     * Sets the user agent name for the POST form header.
     *
     * @param userAgent The user name string for the POST form header.
     */
    public void setUserAgent(String userAgent) {
	this.userAgent = userAgent;
    }

    /**
     * Returns the number of mime parts stored in this instance for
     * inclusion into the POST form.
     *
     * @return The number of mime parts.
     */
    public int getMimePartCount() {
	return mimeParts.size();
    }

    /**
     * Creates the postBody value.
     */
    private void createPostBody() {
	try {
	    CharArrayWriter o = new CharArrayWriter();
	    /* the MIME parts */
	    for (int i = 0; i < mimeParts.size(); i++) {
		((MimePartTuple) mimeParts.get(i)).writeMimePart(o);
	    }
	    /* the footer */
	    o.write("--");
	    o.write(mimeBoundary);
	    o.write("--\r\n");
	    postBody = o.toCharArray();
	    if (debug) {
		BufferedWriter w = new BufferedWriter(new FileWriter(new File("debug.postbody")));
		w.write(postBody);
		w.close();
	    }
	} catch (IOException e) {
	    ; // can only actually come from debug output, so ignore
	}
    }

    /**
     * Creates the postHeader value.
     */
    private void createPostHeader() {
	try {
	    CharArrayWriter o = new CharArrayWriter();
	    o.write("POST " + url.getFile() + " HTTP/1.0\r\n");
	    o.write("Referer: " + url + "\r\n");
	    o.write("Connection: close\r\n");
	    if (userAgent != null) {
		o.write("User-Agent: " + userAgent + "\r\n");
	    }
	    int port = getPort();
	    if (port < 0) {
		o.write("Host: " + url.getHost() + "\r\n");
	    } else {
		o.write("Host: " + url.getHost() + ":" + port + "\r\n");
	    }
	    o.write("Accept: */*\r\n");
//			o.write("Accept-Encoding: gzip\r\n");
	    o.write("Accept-Language: en\r\n");
	    o.write("Accept-Charset: iso-8859-1,*,utf-8\r\n");
//			o.write("Content-type: multipart/binary; boundary=");
	    o.write("Content-type: multipart/form-data; boundary=");
	    o.write(mimeBoundary);
	    o.write("\r\nContent-Length: " + postBody.length + "\r\n");
	    o.write("\r\n");
	    postHeader = o.toCharArray();
	    if (debug) {
		BufferedWriter w = new BufferedWriter(new FileWriter(new File("debug.postheader")));
		w.write(postHeader);
		w.flush();
		w.close();
	    }
	} catch (IOException e) {
	    ; // can only actually come from debug output, so ignore
	}
    }

    /**
     * Invents a new mime boundary string.
     */
    private void createMimeBoundary() {
	/* the boundary string should really be dependent on the data */
	/* ---------------------------15687728176813469052037130799 */
	StringBuffer boundary = new StringBuffer("---------------------------");
	Random r = new Random();
	for (int i = 0; i < 29; i++) {
	    boundary.append(r.nextInt(10));
	}

	int len = boundary.length();
	mimeBoundary = new char[len];
	boundary.getChars(0, len, mimeBoundary, 0);
    }

    /**
     * This class stores the information about a single mime part to be posted.
     */
    private class MimePartTuple {
	private String designator;	// the designator string
	private char[] data;		// the mime part body
	private boolean binary = true;	// is this binary data?

	MimePartTuple(String designator, char[] data) {
	    this.designator = designator;
	    this.data = data;
	}

	/**
	 * Equivalent to
	 * <CODE>MimePartTuple(designator, data.toCharArray())</CODE>.
	 */
	MimePartTuple(String designator, String data) {
	    this(designator, data.toCharArray());
	}

	public String getDesignator() {
	    return designator;
	}

	public char[] getData() {
	    return data;
	}

	public void writeMimePart(Writer o) throws IOException {
	    o.write("--");
	    o.write(mimeBoundary);
	    o.write("\r\n");
	    o.write("Content-Disposition: form-data; name=\"");
	    o.write(designator);
	    o.write("\"");
	    if (binary) {
		o.write("; filename=\"");
		o.write(designator);
		o.write("\"");
	    }
	    o.write("\r\n");
	    if (binary) {
		o.write("Content-Type: application/octet-stream\r\n");
		o.write("Content-Transfer-Encoding: base64\r\n");
//				  o.write("Content-Type: binary\r\n");
	    }
	    o.write("\r\n");
	    o.write(data);
	    if (data.length > 0 && (data.length < 2 || !(data[data.length - 2] == '\r' && data[data.length - 1] == '\n'))) {
		o.write("\r\n");
	    }
//		  	o.write("\r\n");
	}
    }

    /**
     * Executes a simple test program.  It will POST to the URL that is the
     * first argument on the command line.  The user agent will be fetched from
     * the <CODE>user.name</CODE> system property, the contents will be a
     * single mime part with a <CODE>"SHOUT"</CODE> designator and a single line
     * body of <CODE>"marco"</CODE>, and the expected response is
     * <CODE>"polo"</CODE>.
     *
     * @param args The first argument should be the URL to post to
     * @throws IOException Something bad happened.
     */
    public static void main(String[] args) throws IOException {
	URL url = new URL(args[0]);
	PostMethod pm = new PostMethod(url);
	pm.setUserAgent(System.getProperty("user.name"));
	pm.addMimePart("SHOUT", "marco");
	pm.setExpectedAnswer("polo");
	System.out.println("*** URL = " + pm.getURL());
	System.out.print("*** execute returns ");
	System.out.flush();
	System.out.println(pm.execute());
	System.out.println("*** pm.postHeader ***\n" + new String(pm.postHeader));
	System.out.println("*** pm.postBody ***\n" + new String(pm.postBody));
	System.out.println("*** pm.getReplyHeader() ***\n" + new String(pm.getReplyHeader()));
	System.out.println("*** pm.getReplyBody() ***\n" + new String(pm.getReplyBody()));
	System.exit(pm.wasSuccessful() ? 0 : -1);
    }
}
