package edu.pbj.loanapproval.mockapi;

public class FailureModeRequest {
    private boolean enabled;

    public FailureModeRequest() {}

    public FailureModeRequest(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

