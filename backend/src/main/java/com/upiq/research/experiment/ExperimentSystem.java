package com.upiq.research.experiment;

/** Describes one information condition; implementations do not own model/configuration or generation. */
public interface ExperimentSystem {
    ExperimentSystemId systemId();

    default SystemInput createInput(ExperimentCase experimentCase) {
        return SystemInput.forCase(systemId(), experimentCase);
    }
}
