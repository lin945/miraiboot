package top.gardel.howdoi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class HowDoI {
    public static List<String> getAnswerLinks(String baseUrl, Document document, int numOfResult) {
        List<String> result = new ArrayList<>();
        Elements elements = document.getElementsByAttributeValueStarting("id", "question-summary-");
        for (int i = 0; i < Math.min(elements.size(), numOfResult); i++) {
            Element element = elements.get(i);
            Elements a_Link = element.getElementsByClass("question-hyperlink").eq(0);
            if (!a_Link.isEmpty()) {
                String href = a_Link.attr("href");
                if (!href.isEmpty()) {
                    if (href.startsWith("http")) result.add(href);
                    else if (href.startsWith("//")) result.add("https:" + href);
                    else result.add(baseUrl + href);
                }
            }
        }
        return result;
    }

    public static List<Answer> getAnswer(RestTemplate restTemplate, String answerUrl, int perQuestion) {
        String content;
        try {
            content = restTemplate.getForObject(answerUrl, String.class);
            if (content == null) return Collections.emptyList();
        } catch (RestClientException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
        //System.out.println(content);
        Document document = Jsoup.parse(content, answerUrl);
        Optional<Element> titleElement = Optional.ofNullable(document.getElementById("question-header"))
            .map(element -> element.getElementsByClass("question-hyperlink").eq(0).first());
        List<Answer> result = new ArrayList<>();
        Answer answer = new Answer();
        answer.setAnswerUrl(answerUrl.substring(0, answerUrl.indexOf("?")));
        titleElement.ifPresent(element -> answer.setTitle(element.text()));
        Optional<Element> questionElement = Optional.ofNullable(document.getElementById("question"))
            .map(element -> element.getElementsByClass("js-post-body").eq(0).first());
        questionElement.ifPresent(element -> answer.setQuestion(element.text()));
        Optional<List<Element>> answerElements;
        answerElements = Optional.ofNullable(document.getElementsByAttributeValueStarting("id", "answer-"))
            .map(Collection::stream)
            .map(elementStream -> elementStream.map(element -> element.getElementsByClass("js-post-body").eq(0).first()).collect(Collectors.toList()));
        answerElements.ifPresent(elementList -> {
            for (int i = 0, elementListSize = elementList.size(); i < Math.min(elementListSize, perQuestion); i++) {
                Element element = elementList.get(i);
                Answer a = new Answer();
                a.setTitle(answer.getTitle());
                a.setQuestion(answer.getQuestion());
                String answerId = element.parent().parent().parent().attr("data-answerid");
                a.setAnswerUrl(answer.getAnswerUrl() + "/" + answerId + "#" + answerId);
                a.setAnswer(getTextFromHtml(element));
                result.add(a);
            }
        });
        return result;
    }

    private static String getTextFromHtml(Element element) {
        final StringBuilder accum = new StringBuilder();
        NodeTraversor.traverse(new NodeVisitor() {
            public void head(Node node, int depth) {
                if (node instanceof TextNode) {
                    TextNode textNode = (TextNode) node;
                    accum.append(textNode.getWholeText());
                } else if (node instanceof Element) {
                    Element element = (Element) node;
                    if (accum.length() > 0 &&
                        (element.isBlock() || element.tag().getName().equals("br")) &&
                        !Character.isWhitespace(accum.charAt(accum.length() - 1)))
                        accum.append('\n');
                }
            }

            public void tail(Node node, int depth) {
                // make sure there is a space between block tags and immediately following text nodes <div>One</div>Two should be "One Two".
                if (node instanceof Element) {
                    Element element = (Element) node;
                    if (element.isBlock() && (node.nextSibling() instanceof TextNode) && !Character.isWhitespace(accum.charAt(accum.length() - 1)))
                        accum.append(' ');
                }

            }
        }, element);
        return accum.toString().trim();
    }
}
