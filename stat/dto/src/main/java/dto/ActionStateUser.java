package dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionStateUser {
    SEND_TO_REVIEW, CANCEL_REVIEW;

    @JsonCreator
    public static ActionStateUser forValues(@JsonProperty("stateAction") String name) {
        for (ActionStateUser action : ActionStateUser.values()) {
            if (action.name().equals(name)) {
                return action;
            }
        }
        return null;
    }
}
