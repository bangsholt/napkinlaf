/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 21, 2002
 * Time: 10:45:10 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package gameMaster.impl.chat;

import gameMaster.impl.ServerMove;

public class ChatUtter extends ServerMove {
    private String utterance;
    private String speaker;

    public ChatUtter(String speaker, String utterance) {
        super(speaker);
        this.speaker = speaker;
        this.utterance = utterance;
    }

    public String getUtterance() {
        return utterance;
    }

    public String getSpeaker() {
        return speaker;
    }

    // inherit doc comment
    public String toString() {
        return "Utter: \"" + utterance + '"';
    }
}
