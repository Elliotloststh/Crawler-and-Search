import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class MyCrawler extends WebCrawler {
    private final static Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(css|js|gif|jpg|png|mp3|mp4|zip|gz)$");
    String[] candiurl = {"https://iask.sina.com.cn/c/202", "https://iask.sina.com.cn/c/986", "https://iask.sina.com.cn/c/990",
            "https://iask.sina.com.cn/c/983", "https://iask.sina.com.cn/c/985", "https://iask.sina.com.cn/c/987",
            "https://iask.sina.com.cn/c/991", "https://iask.sina.com.cn/c/993", "https://iask.sina.com.cn/c/203",
            "https://iask.sina.com.cn/c/989", "https://iask.sina.com.cn/c/992","https://iask.sina.com.cn/c/988",
            "https://iask.sina.com.cn/c/984"};
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        // Ignore the url if it has an extension that matches our defined set of image extensions.
        if (IMAGE_EXTENSIONS.matcher(href).matches()) {
            return false;
        }

        for(String s:candiurl) {
            if(href.startsWith(s)) {
                return true;
            }
        }

        return href.startsWith("https://iask.sina.com.cn/b/");

    }
    @Override
    protected boolean shouldFollowLinksIn(WebURL url) {
        String href = url.getURL().toLowerCase();
        for(String s:candiurl) {
            if(href.startsWith(s)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void visit(Page page) {
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        String url = page.getWebURL().getURL();

        if(url.startsWith("https://iask.sina.com.cn/c/"))
            return;
        if (page.getParseData() instanceof HtmlParseData) {
            // get a unique name for storing this image

            String url2file = url.replace('/','-');

            // store image
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String html = htmlParseData.getHtml();//页面html内容
            Document doc = Jsoup.parse(html);
//            Elements title1 = doc.getElementsByClass("question-title");
            Element title1 = doc.getElementById("paramDiv");
            Elements answer1 = doc.getElementsByClass("new-answer-text new-answer-cut new-pre-answer-text").select("pre");
            Elements answer2 = doc.getElementsByClass("answer-info");
            Elements user_name1 = doc.select("ul.new-answer-list").select("div.user-info");
            Elements ans_time1 = doc.select("ul.new-answer-list").select("p.time");
            Elements types = doc.getElementsByClass("breadcast-fl");
            try {
                String filename0 = "/Users/elliot/Documents/Java/作业/hw4/data/";
                if(types.size() <= 1) {
                    return;
                }
                if(types.size() >= 1){
                    String tmp = types.get(0).selectFirst("a").text();
                    if(!tmp.equals("教育/科学")){
                        return;
                    }
                    filename0 += "教育科学";
                }
                if(types.size() >= 2){
                    String tmp = types.get(1).selectFirst("a").text();
                    if(!tmp.equals("理工学科")){
                        return;
                    }
                    filename0 += "/" + types.get(1).selectFirst("a").text();
                }
                if(types.size() >= 3){
                    filename0 += "/" + types.get(2).selectFirst("a").text();
                }
                filename0 += ".txt";
                File filename = new File(filename0);
                if (!filename.getParentFile().getParentFile().exists()) {//判断路径是否存在，如果不存在，则创建上一级目录文件夹
                    filename.getParentFile().getParentFile().mkdirs();
                }
                if (!filename.getParentFile().exists()) {//判断路径是否存在，如果不存在，则创建上一级目录文件夹
                    filename.getParentFile().mkdirs();
                }
                System.out.println(url);
                FileWriter fileWriter=new FileWriter(filename, true);
                fileWriter.write("==========url==========" + "\r\n");
                fileWriter.write(url + "\r\n");
//                fileWriter.write("==========url_end==========" + "\r\n");
                fileWriter.write("==========title==========" + "\r\n");
//                for(Element t:title1) {
//                    if(t.text().length()!=0 && !t.toString().contains("no-title"))
//                        fileWriter.write(t.text() + "\r\n");
//                }
                if(title1.attr("qcontent").length()!=0)
                    fileWriter.write(title1.attr("qcontent") + "\r\n");
//                fileWriter.write("==========title_end==========" + "\r\n");
                fileWriter.write("==========answer==========" + "\r\n");
                int counter = 0;
                boolean flag = false;


                if(user_name1.size()!=0) {
                    for(Element a:answer1) {
                        if(flag) {
                            counter++;
                            if(user_name1.get(counter-1).select("div.name-txt").size() == 0) {
                                fileWriter.write("【答案" + counter + "】\r\n" + a.toString().replaceAll("<br\\s*/?>", "\r\n").replaceAll("[ ]|[ ]","").replaceAll("<[.[^>]]*>","")+ "\r\n"
                                        + "回答者：" + user_name1.get(counter-1).selectFirst("p.user-name").selectFirst("a").text() + "\r\n"
                                        + "回答时间：" + ans_time1.get(counter-1).text()  + "\r\n");
                            } else {
                                fileWriter.write("【答案" + counter + "】\r\n" + a.toString().replaceAll("<br\\s*/?>", "\r\n").replaceAll("[ ]|[ ]","").replaceAll("<[.[^>]]*>","")+ "\r\n"
                                        + "回答者：" + user_name1.get(counter-1).selectFirst("div.name-txt").selectFirst("a").text() + "\r\n"
                                        + "回答时间：" + ans_time1.get(counter-1).text()  + "\r\n");
                            }

                        }
                        flag = true;
                    }
                } else {
                    for(Element a:answer2) {
                        counter++;
                        Elements tmp1 = a.selectFirst("div.answer_text").select("pre");
                        int cur = 0;
                        if(tmp1.size() == 1) {
                            cur = 0;
                        }else if(tmp1.size() == 2) {
                            cur = 1;
                        }
                        Elements tmp2 = a.select("div.text-con.fl");
                        if(tmp2.size() != 0) {
                            fileWriter.write("【答案" + counter + "】\r\n" + a.selectFirst("div.answer_text").select("pre").get(cur).toString().replaceAll("<br\\s*/?>", "\r\n").replaceAll("[ ]|[ ]","").replaceAll("<[.[^>]]*>","")+ "\r\n"
                                    + "回答者：" + a.select("div.text-con.fl").select("span.fl").text()+"-"+a.select("div.text-con.fl").select("span.company-name.fl").text() + "\r\n"
                                    + "回答时间：" + a.select("div.text-con.fl").select("span.time.fl").text() + "\r\n");
                        } else {
                            fileWriter.write("【答案" + counter + "】\r\n" + a.selectFirst("div.answer_text").select("pre").get(cur).toString().replaceAll("<br\\s*/?>", "\r\n").replaceAll("[ ]|[ ]","").replaceAll("<[.[^>]]*>","")+ "\r\n"
                                    + "回答者：" + a.selectFirst("span.user_wrap").selectFirst("a.author_name").text() + "\r\n"
                                    + "回答时间：" + a.selectFirst("span.answer_t").text() + "\r\n");
                        }
                    }
                }

                fileWriter.write("==========end==========" + "\r\n");
                WebCrawler.logger.info("Stored: {}", filename);
                fileWriter.flush();
                fileWriter.close();
//                String d = "s";
            } catch (IOException iox) {
                WebCrawler.logger.error("Failed to write file: ", iox);
            } catch (IndexOutOfBoundsException i) {
                try {
                    FileWriter f = new FileWriter("/Users/elliot/Documents/Java/作业/hw4/data/fail_log.txt", true);
                    f.write("Failed :" + url + i + "\r\n");
                    f.flush();
                    f.close();
                } catch (IOException io) {

                }
                System.exit(1);
            }

        }

    }

}
