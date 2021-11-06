package de.blinkt.openvpn.eventbus;

public class VpnErrorEvent {
    public final String message;

    public VpnErrorEvent(String message) {
        this.message = message;
    }
}
