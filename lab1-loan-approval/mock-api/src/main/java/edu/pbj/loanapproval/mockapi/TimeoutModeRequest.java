package edu.pbj.loanapproval.mockapi;

public class TimeoutModeRequest {
    private boolean enabled;
    private int delaySeconds;

    public TimeoutModeRequest() {}

    public TimeoutModeRequest(boolean enabled, int delaySeconds) {
        this.enabled = enabled;
        this.delaySeconds = delaySeconds;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getDelaySeconds() {
        return delaySeconds;
    }

    public void setDelaySeconds(int delaySeconds) {
        this.delaySeconds = delaySeconds;
    }
}

