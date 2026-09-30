package com.upiq.research.intent;

import lombok.Value;

/**
 * Pair of typed intent and the verified calculation-engine result.
 */
@Value
public class DispatchedCalculation {
    FinancialQueryIntent intent;
    Object verifiedResult;
}
