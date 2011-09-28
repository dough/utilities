package net.dougharris.utility;

import com.petebevin.markdown.MarkdownProcessor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class ShowCode{
  static StringBuffer b = new StringBuffer();
  static Pattern pamp=Pattern.compile("\\&");
  static Pattern plt=Pattern.compile("\\<");
  static Pattern pgt=Pattern.compile("\\>");
  static P cmdArgs;

// changed from throws IOException to throws Exception
  static public void main(String[] args) throws Exception {
    String options = "h_t_m_p_z_o_";
    String usage = 
      "ShowCode [-t Title] [-h Header] [-z ZipFile] [-o HtmlOutFile] [-m MKDPrefaceFile] codeFile...";
    cmdArgs = P.arseArgs(options, args);

    String htmlTitle        =  cmdArgs.getProperty("-t", "Title");
    String htmlHeader       =  cmdArgs.getProperty("-h", "Header");
    String htmlFileName     = cmdArgs.getProperty("-o", "index.html");
    String zipFileName      =  cmdArgs.getProperty("-z", "");
    String prefaceFileName  =  cmdArgs.getProperty("-p", "");
    String markdownFileName  =  cmdArgs.getProperty("-m", "");
    FileWriter o = new FileWriter(htmlFileName);
    args = cmdArgs.getParams();
    // The args is just a list of files,
    // in the order in which they are to be shown.
    // There will be a <dl> and then for each file
    // a <dt> elements with the fileName
    // followed by a dd element which contains the file contents
    // properly wrapped encapsulated within  <pre><code> tags.
    b.append("<html>\n");
    b.append("<head>\n");
    b.append("<title>");
    b.append(htmlTitle);
    b.append(", by doug@mscs.mu.edu");
    b.append("</title>\n");
    b.append("</head>\n");
    b.append("<body>\n");
    b.append("<h3 align=\"center\">");
    b.append(htmlHeader);
    b.append("</h3>\n");
    b.append("<table align=\"center\" width=\"600\">\n");

    if (!markdownFileName.equals("")){
      b.append("<tr>\n");
      b.append("<td>\n");
      doMarkdown(markdownFileName);
      b.append("</td>\n");
      b.append("</tr>\n");
    }

    if (!prefaceFileName.equals("")){
      b.append("<tr>\n");
      b.append("<td>\n");
      doPreface(prefaceFileName);
      b.append("</td>\n");
      b.append("</tr>\n");
    }

    if (!zipFileName.equals("")){
      b.append("<tr>\n");
      b.append("<td>\n");
      b.append("<a href=\""+zipFileName+"\">zipped source code</a>");
      b.append("</td>\n");
      b.append("</tr>\n");
    }

// Creating the Code links section
    b.append("<tr>\n");
    b.append("<td>\n");
    b.append("<h4><a name=\"top\">Code links</a></h4>\n");
    b.append("<dl>\n");
    for (int j = 0;j<args.length;j++){
      makeFileNameElement(args[j]);
    }
    b.append("</dl>\n");

// Creating the Listings
    b.append("<h4>Listings</h4>");
    b.append("<dl>\n");
// Creating the wrapped code Listings
    for (int j = 0;j<args.length;j++){
      makeFileCodeElement(args[j]);
    }

    b.append("</dl>\n");
    b.append("</td>\n");
    b.append("</tr>\n");
    b.append("</table>\n");
    b.append("</body>\n");
    b.append("</html>");
    o.write(b.toString());
    o.close();
  }

  static public  void makeFileNameElement(String s){
    b.append("<dt><a href=\"#");
    b.append(s);
    b.append("\">");
    b.append(s);
    b.append("</a></dt>\n");
  }

  static public void makeFileCodeElement(String s) throws Exception{
    b.append("<dt><a href=\"#top\" name=\"");
    b.append(s);
    b.append("\">^&nbsp;</a>");
    b.append(s);
    b.append("</dt>\n");
    b.append("<dd>\n");
    makeFileListing(s);
    b.append("</dd>\n");
  }

  static public void makeFileListing(String s) throws Exception{
      String descFileName = s+".mkd";
      File fm = new File(descFileName);
      if (fm.exists()){
        b.append("<dd>\n");
        b.append("<dl>\n");
        b.append("<table>\n");
        doMarkdown(descFileName);       
        b.append("</dd>\n");
        b.append("</dl>\n");
        b.append("</table>\n");
      }
      b.append("<pre><code>");
      File f = new File(s);
      char[] a = new char[(int)f.length()];
      FileReader i = new FileReader(f);
      i.read(a);
      b.append(editFileListing(new String(a)));
      b.append("\n</code></pre>\n");
  }

  static public String editFileListing(String s) throws Exception{
    s = (pamp.matcher(s)).replaceAll("\\&amp;");
    s = (plt.matcher(s)).replaceAll("\\&lt;");
    s = (pgt.matcher(s)).replaceAll("\\&gt;");
    return s;
  }

  static public void doPreface(String prefaceFileName) throws Exception{
    b.append("<tr>\n");
    b.append("<td>\n");
    BufferedReader i = new BufferedReader(new FileReader(prefaceFileName));
    String line;
    while(null!=(line=i.readLine())){
      b.append(line);
      b.append("\n");
    }
    b.append("</td>\n");
    b.append("</tr>\n");
  }

  static public void doMarkdown(String markdownFileName) throws Exception{ 
    b.append("<tr>\n");
    b.append("<td>\n");
    FileInputStream i = new FileInputStream(markdownFileName);
    String in = allin(i);

    MarkdownProcessor m = new MarkdownProcessor();
    b.append(m.markdown(in));
    b.append("</td>\n");
    b.append("</tr>\n");
  }

  static public String allin(FileInputStream i) throws Exception{
    char[] b = new char[i.available()];
    InputStreamReader r = new InputStreamReader(i);  
    r.read(b);  
    return(new String(b));
  }
}
