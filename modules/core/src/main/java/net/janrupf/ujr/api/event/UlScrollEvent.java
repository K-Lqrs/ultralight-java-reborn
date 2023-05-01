package net.janrupf.ujr.api.event;

// TODO: fix doc comment references

/**
 * A generic scroll event.
 *
 * @see View::FireScrollEvent
 */
public class UlScrollEvent {
    /**
     * Scroll granularity type.
     */
    public UlScrollEventType type;

    /**
     * Horizontal scroll amount.
     */
    public int deltaX;

    /**
     * Vertical scroll amount.
     */
    public int deltaY;
}
