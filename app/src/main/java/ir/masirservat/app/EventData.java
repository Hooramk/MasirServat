package ir.masirservat.app;
import java.util.ArrayList;
import java.util.List;

public class EventData {
    public String id, chapter, speaker, title, description;
    public List<Choice> choices = new ArrayList<>();

    public static class Choice {
        public String id, title;
    }
}
