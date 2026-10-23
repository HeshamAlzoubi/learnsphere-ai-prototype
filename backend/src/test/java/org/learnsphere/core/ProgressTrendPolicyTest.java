package org.learnsphere.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProgressTrendPolicyTest {
    @Test void improving() { assertEquals("IMPROVING", ProgressTrendPolicy.classify(100, 67)); }
    @Test void declining() { assertEquals("DECLINING", ProgressTrendPolicy.classify(33, 67)); }
    @Test void unchanged() { assertEquals("UNCHANGED", ProgressTrendPolicy.classify(67, 67)); }
    @Test void missingHistory() {
        assertEquals("INSUFFICIENT_DATA", ProgressTrendPolicy.classify(null, null));
        assertEquals("INSUFFICIENT_DATA", ProgressTrendPolicy.classify(100, null));
    }
    @Test void invalidScoreRejected() {
        assertThrows(IllegalArgumentException.class, () -> ProgressTrendPolicy.classify(101, 50));
    }
}
