import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    public static void main(String[] args) throws Exception {
        String crawlStorageFolder = "../Crawler/crawldata";
        int numberOfCrawlers = 8;//线程数量

        CrawlConfig config = new CrawlConfig();
        config.setMaxDepthOfCrawling(2);
        config.setCrawlStorageFolder(crawlStorageFolder);
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);//创建爬虫执行器

        String[] candiurl = {"https://iask.sina.com.cn/c/202.html", "https://iask.sina.com.cn/c/986.html", "https://iask.sina.com.cn/c/990.html",
                "https://iask.sina.com.cn/c/983.html", "https://iask.sina.com.cn/c/985.html", "https://iask.sina.com.cn/c/987.html",
                "https://iask.sina.com.cn/c/991.html", "https://iask.sina.com.cn/c/993.html", "https://iask.sina.com.cn/c/203.html",
                "https://iask.sina.com.cn/c/989.html", "https://iask.sina.com.cn/c/992.html","https://iask.sina.com.cn/c/988.html",
                "https://iask.sina.com.cn/c/984.html"};
        for(String s:candiurl) {
            controller.addSeed(s);
        }
//        controller.addSeed("https://iask.sina.com.cn/b/1SXgfNj46V6L.html");
        config.setMaxDepthOfCrawling(3);
        config.setPolitenessDelay(0);
        config.setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.0 Safari/605.1.15");
//        config.setResumableCrawling(true);
        controller.start(MyCrawler.class, numberOfCrawlers);//开始执行爬虫
    }
}
