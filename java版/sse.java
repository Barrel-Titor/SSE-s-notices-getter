import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by hongjiayong on 16/3/8.
 */
public class sse {
    static java.lang.String url = "http://sse.tongji.edu.cn/InfoCenter/Lastest_Notice.aspx";
    static ArrayList<String> title = new ArrayList<String>();
    static ArrayList<String> notice = new ArrayList<String>();
    static int currentPage = 1;
    static Connection con = Jsoup.connect(url);

    static JFrame frame = new JFrame("SSE通知获取器");
    static TextArea noticesList = new TextArea("", 60, 60, 1);
    static TextArea noticeContent = new TextArea("", 60, 60, 1);
    static JTextField putIn = new JTextField(10);

    public static void main(String arg[]) throws IOException {
        //Setting header
        con = setHeader(con);
        //Get html
        System.out.println("Loading...");
        final Document doc = con.get();
        //Get Notice's title and Notice's url
        for (int i = 0; i < 20; i++){
            Element item = doc.getElementById("GridView1_HyperLink1_" + String.valueOf(i));
            title.add(item.text());
            notice.add("http://sse.tongji.edu.cn/" + item.attr("href").substring(3));
        }

        //UI
        frame.setLayout(new GridLayout(7, 1));
        //noticesLabel
        JLabel noticesLabel = new JLabel("学院新闻:");
        frame.add(noticesLabel);
        //noticesList
        frame.add(noticesList);
        //noticeLabel
        JLabel noticeLabel = new JLabel("通知详情:");
        frame.add(noticeLabel);
        //noticeContent
        frame.add(noticeContent);
        //putIn
        frame.add(putIn);
        putIn.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER){
                    String command = putIn.getText();
                    putIn.setText("");
                    if(command.equals("exit")){
                        System.exit(0);
                    }
                    try{
                        int noticeNumber = Integer.parseInt(command);
                        if(noticeNumber < 1 || noticeNumber > 20){
                            noticeContent.setText("请求越界!");
                            return;
                        }
                        noticeContent.setText("");
                        getContent(noticeNumber - 1);
                    }
                    catch (NumberFormatException NFE){
                        noticeContent.setText("");
                        noticeContent.append("请输入数字!");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        //Buttons
        Container buttonPane = new Container();
        buttonPane.setLayout(new FlowLayout());
        JButton buttonPre = new JButton("Pre");
        buttonPre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    turnPre(doc);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        buttonPane.add(buttonPre);
        JButton buttonQuit = new JButton("Quit");
        buttonQuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        buttonPane.add(buttonQuit);
        JButton buttonNext = new JButton("Next");
        buttonNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    turnNext(doc);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        buttonPane.add(buttonNext);
        frame.add(buttonPane);

        frame.pack();
        frame.setVisible(true);
        noticesList.setText("第" + String.valueOf(currentPage) + "页\n");
        for (int i = 0; i < title.size(); i++) {
            noticesList.append(String.valueOf(i + 1) + " " + title.get(i) + "\n");
        }
    }


    static Connection setHeader(Connection open){
        open.timeout(8000)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Accept-Encoding", "gzip, deflate, sdch")
                .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6")
                .header("Connection" , "keep-alive")
                .header("Host", "sse.tongji.edu.cn")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36");
        return open;
    }

    static void getContent(int pageNumber) throws IOException {
        Connection page = Jsoup.connect(notice.get(pageNumber));
        page = setHeader(page);
        Document doc = page.get();
        Element contents = doc.getElementById("content");
        String article = title.get(pageNumber) + "\n";
        for (int i = 0; i < contents.childNodeSize(); i++){
            article += readContent(contents.childNode(i));
        }

        noticeContent.setText(article);
    }

    static String readContent(Node con){
        String article = "";
        if (con.nodeName().equals("#text")){
            String deal = con.toString();
            article += deal.replaceAll("&nbsp;"," ");
        }
        else if(con.nodeName().equals("br")){
            article += "\n";
        }
        for(int i = 0; i < con.childNodeSize(); i++){
            if (con.childNode(i).nodeName().equals("#text")){
                String deal = con.childNode(i).toString();
                article += deal.replaceAll("&nbsp;"," ");
                continue;
            }
            else if(con.childNode(i).nodeName().equals("br")){
                article += "\n";
                continue;
            }
            article += readContent(con.childNode(i));

        }
        return article;
    }

    static void turnPre(Document doc) throws IOException {
        if (currentPage - 1 < 0){
            noticeContent.setText("当前为第一页!");
            return;
        }
        else
            currentPage--;

        title.clear();
        notice.clear();

        Connection open = Jsoup.connect(url);
        open = setHeader(open);
        Document newpage = open.data("__VIEWSTATE", doc.getElementById("__VIEWSTATE").attr("value"))
                .data("__VIEWSTATEGENERATOR", doc.getElementById("__VIEWSTATEGENERATOR").attr("value"))
                .data("__EVENTVALIDATION", doc.getElementById("__EVENTVALIDATION").attr("value"))
                .data("__EVENTTARGET", "GridView1$ctl23$AspNetPager1")
                .data("__EVENTARGUMENT", String.valueOf(currentPage))
                .data("txtTitle", "")
                .data("ddlPeriod", "0")
                .post();

        for (int i = 0; i < 20; i++){
            Element item = newpage.getElementById("GridView1_HyperLink1_" + String.valueOf(i));
            title.add(item.text());
            notice.add("http://sse.tongji.edu.cn/" + item.attr("href").substring(3));
        }
        noticesList.setText("第" + String.valueOf(currentPage) + "页\n");
        for (int i = 0; i < title.size(); i++) {
            noticesList.append(String.valueOf(i + 1) + " " + title.get(i) + "\n");
        }
    }

    static void turnNext(Document doc) throws IOException {
        currentPage++;
        title.clear();
        notice.clear();

        Connection open = Jsoup.connect(url);
        open = setHeader(open);
        Document newpage = open.data("__VIEWSTATE", doc.getElementById("__VIEWSTATE").attr("value"))
                .data("__VIEWSTATEGENERATOR", doc.getElementById("__VIEWSTATEGENERATOR").attr("value"))
                .data("__EVENTVALIDATION", doc.getElementById("__EVENTVALIDATION").attr("value"))
                .data("__EVENTTARGET", "GridView1$ctl23$AspNetPager1")
                .data("__EVENTARGUMENT", String.valueOf(currentPage))
                .data("txtTitle", "")
                .data("ddlPeriod", "0")
                .post();

        for (int i = 0; i < 20; i++){
            Element item = newpage.getElementById("GridView1_HyperLink1_" + String.valueOf(i));
            title.add(item.text());
            notice.add("http://sse.tongji.edu.cn/" + item.attr("href").substring(3));
        }
        noticesList.setText("第" + String.valueOf(currentPage) + "页\n");
        for (int i = 0; i < title.size(); i++) {
            noticesList.append(String.valueOf(i + 1) + " " + title.get(i) + "\n");
        }
    }
}
