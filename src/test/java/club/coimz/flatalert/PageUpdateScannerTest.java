package club.coimz.flatalert;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageUpdateScannerTest {

    @Test
    @Disabled
    void checkCoop() throws IOException {

//
//        Document doc = Jsoup.connect("https://www.sozialbau.at/angebot/sofort-verfuegbar/").get();
//        Element masthead = doc.select("div.tx-wx-sozialbau").first();
//        System.out.println(masthead);

        Document doc = Jsoup.connect("https://www.migra.at/immobilienangebot/wohnen/").get();
        Element masthead = doc.select("ul.ResidentialRealtyListBlock").first();
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