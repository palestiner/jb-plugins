package dev.palestiner.jbplugins.state;

public interface OrderedState {

    StateOrder order();

    enum StateOrder implements Comparable<StateOrder> {
        CHECK_ARGUMENTS_STATE(10),
        PLUGIN_SELECTION_STATE(20),
        VERSION_RETRIEVAL_STATE(30),
        VERSION_SELECTION_STATE(40),
        FAMILY_SELECTION_STATE(50),
        DOWNLOAD_AND_FINISH_STATE(60);

        private final int value;

        StateOrder(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }

}
