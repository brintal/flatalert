package club.coimz.flatalert;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class PageUpdateScannerTest {

    @Test
    @Disabled
    void checkCoopJavascript() throws IOException {

//
//        Document doc = Jsoup.connect("https://www.sozialbau.at/angebot/sofort-verfuegbar/").get();
//        Element masthead = doc.select("div.tx-wx-sozialbau").first();
//        System.out.println(masthead);

        WebClient webClient = new WebClient();
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        HtmlPage myPage = webClient.getPage("https://www.migra.at/immobilienangebot/wohnen/");

        // convert page to generated HTML and convert to document
        Document doc = Jsoup.parse(myPage.asXml());

        webClient.close();

//        Document doc = Jsoup.connect("https://www.bwsg.at/overview/?status_in_planning=false&status_under_contruction=false&status_immediately=true&search_text=Wien&buy_exclusive=false&rent_exclusive=true&price-min=0&price-max=1000&rooms-min=3&rooms-max=5&living_space-min=80&living_space-max=150&has_apartment=true").get();
        Element masthead = doc.select("ul.ResidentialRealtyListBlock").first();
        System.out.println("PRINTING OUTPUT!!!!1");
        System.out.println(masthead);
    }

    @Test
    @Disabled
    void checkCoop() throws IOException {

//
        Document doc = Jsoup.connect("https://www.wogem.at/de/angebote.php").get();
        Element masthead = doc.select("body").first();
        System.out.println(masthead);
    }


    @Test
    @Disabled
    void diffTest() throws IOException {

        Document doc = Jsoup.connect("https://www.sozialbau.at/angebot/sofort-verfuegbar/").get();
        Element masthead = doc.select("div.tx-wx-sozialbau").first();

        List orig = Arrays.asList(masthead.toString().split("\\n"));
        System.out.println(orig);

        List alt = new ArrayList(orig);
        alt.add(1, "this is an added line");
        alt.remove(4);


        //compute the patch: this is the diffutils part
        Patch<String> patch = DiffUtils.diff(orig, alt);

//simple output the computed patch to console
        for (AbstractDelta<String> delta : patch.getDeltas()) {
            System.out.println(delta);
        }


    }


}