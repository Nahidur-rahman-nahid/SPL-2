package com.TimeWise.utils;

import lombok.Getter;
import lombok.Setter;

public class TeamRelatedRequestBody {
    @Getter
    @Setter
    public static class TeamJoiningInvitation{
        private String teamName;
        private String invitedTo;
        private String invitationMessage;
    }
    @Getter
    @Setter
    public static class ReplyToTeamJoiningInvitation{
        private String teamName;
        private String response;
    }

}
