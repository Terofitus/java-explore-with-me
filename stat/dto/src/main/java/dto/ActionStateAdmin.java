package dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionStateAdmin {
    PUBLISH_EVENT, REJECT_EVENT;

    @JsonCreator
    public static ActionStateAdmin forValues(@JsonProperty("stateAction") String name) {
        for (ActionStateAdmin action : ActionStateAdmin.values()) {
            if (action.name().equals(name)) {
                return action;
            }
        }
        return null;
    }
}
