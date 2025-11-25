package com.imran.edcassistant.service.remote;

import com.imran.edcassistant.model.AccessPolicy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
public class EdcPolicyBuilder {

    public Map<String, Object> buildPolicy(
            String policyId,
            String assetId,
            AccessPolicy policy
    ) {

        return Map.of(
                "id", policyId,
                "policy", Map.of(
                        "permissions", List.of(
                                Map.of(
                                        "edctype", "odrl:Permission",
                                        "target", assetId,
                                        "action", Map.of("type", "use"),
                                        "constraints", List.of(
                                                Map.of(
                                                        "edctype", "AtomicConstraint",
                                                        "leftExpression", Map.of("value", "ParticipantId"),
                                                        "operator", "in",
                                                        "rightExpression", Map.of("value", policy.getAllowedCompanies())
                                                ),
                                                Map.of(
                                                        "edctype", "AtomicConstraint",
                                                        "leftExpression", Map.of("value", "purposelabel"),
                                                        "operator", "eq",
                                                        "rightExpression", Map.of("value", policy.getUsagePurpose())
                                                )
                                        )
                                )
                        )
                )
        );
    }
}
