package club.coimz.flatalert;


import com.github.difflib.DiffUtils;
import com.github.difflib.patch.*;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PageUpdateScanner {

    private final CoopsConfiguration coopsConfiguration;
    private final AlertConfiguration alertConfiguration;

    private Map<String, String> contentMap = new HashMap<>();

    private static final String ALERT_TEMPLATE = "New update for %s! %s";
    private static final String INIT_TEMPLATE = "Starting to watch %s! %s";

    private TelegramBot bot;

    @PostConstruct
    public void init() throws IOException {
        bot = new TelegramBot(alertConfiguration.getToken());
        for (CoopConfig coopConfig : coopsConfiguration.getConfigs()) {
            Document doc = Jsoup.connect(coopConfig.getUrl()).get();
            String content = doc.select(coopConfig.getSelector()).first().toString();
            contentMap.put(coopConfig.getName(), content);
            log.info("starting to watch " + coopConfig.getName());
            sendAlert(INIT_TEMPLATE, coopConfig);
        }
    }

    @Scheduled(fixedDelayString = "${execution.intervalMillis}")
    public void checkForUpdates() {
        coopsConfiguration.getConfigs().forEach(this::checkCoop);
    }


    public void checkCoop(CoopConfig coopConfig) {
        try {
            Document doc = Jsoup.connect(coopConfig.getUrl()).get();
            String content = doc.select(coopConfig.getSelector()).first().toString();

            if (!contentMap.get(coopConfig.getName()).equals(content)) {
                log.info("found update for " + coopConfig.getName() + ". sending alert...");
                String diffMessage = calculateDiffMessage(contentMap.get(coopConfig.getName()), content);
                contentMap.put(coopConfig.getName(), content);
                sendAlert(ALERT_TEMPLATE, diffMessage, coopConfig);
            } else {
                log.info("no updates for " + coopConfig.getName());
            }


        } catch (Exception e) {
            log.error("error while checking " + coopConfig);
        }
    }

    private String calculateDiffMessage(String oldContent, String newContent) {

        List<String> oldLines = Arrays.asList(oldContent.split("\\n"));
        List<String> newLines = Arrays.asList(newContent.split("\\n"));

        Patch<String> patch = DiffUtils.diff(oldLines, newLines);

        StringBuilder added = new StringBuilder();
        StringBuilder removed = new StringBuilder();
        StringBuilder changed = new StringBuilder();

        for (AbstractDelta<String> delta : patch.getDeltas()) {
            if (delta instanceof DeleteDelta) {
                addLinesToBuilder(removed, delta.getSource().getLines());
            } else if (delta instanceof InsertDelta) {
                addLinesToBuilder(added, delta.getTarget().getLines());
            } else if (delta instanceof ChangeDelta) {
                addLinesToBuilder(changed, delta.getTarget().getLines());
            }
        }

        StringBuilder total = new StringBuilder();

        if (removed.length() > 0) {
            total.append("\nremoved: \n");
            total.append(removed.toString());
        }
        if (added.length() > 0) {
            total.append("\nadded: \n");
            total.append(added.toString());
        }
        if (changed.length() > 0) {
            total.append("\nchanged: \n");
            total.append(changed.toString());
        }
        return total.toString()
                .replaceAll("\\<.*?>", "")
                .replaceAll("&nbsp", " ");
    }


    private void addLinesToBuilder(StringBuilder builder, List<String> lines) {
        lines.forEach(str -> {
            if (StringUtils.isNotBlank(str.trim())) {
                builder.append(str);
                builder.append("\n");
            }
        });
    }

    private void sendAlert(String messageTemplate, CoopConfig coopConfig) {
        sendAlert(messageTemplate, "", coopConfig);
    }

    private void sendAlert(String messageTemplate, String additionalMessage, CoopConfig coopConfig) {

        String message = String.format(messageTemplate, coopConfig.getName(), coopConfig.getUrl()) + additionalMessage;

        log.info("sending message: " + message);

        try {
            SendMessage request = new SendMessage(alertConfiguration.getChatId(), message);
            request.disableWebPagePreview(true);
            SendResponse sendResponse = bot.execute(request);
            log.info("sent notification to telegram " + sendResponse.message());
        } catch (Exception e) {
            log.warn("error sending telegram message", e);
        }
    }
}
