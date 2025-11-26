package com.imran.edcassistant.service;

import com.imran.edcassistant.model.dto.AccessPolicy;
import com.imran.edcassistant.model.edc.EdcConstraint;
import com.imran.edcassistant.model.edc.EdcPermission;
import com.imran.edcassistant.model.edc.EdcPolicy;
import com.imran.edcassistant.model.edc.EdcPolicyDefinition;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PolicyService {

    public EdcPolicyDefinition createEdcPolicy(String policyId, AccessPolicy accessPolicy) {
        EdcPolicyDefinition policyDefinition = new EdcPolicyDefinition();
        policyDefinition.setId(policyId);

        EdcPolicy policy = new EdcPolicy();
        policy.setPermission(createPermission(accessPolicy));
        policyDefinition.setPolicy(policy);
        return policyDefinition;

    }

    private List<EdcPermission> createPermission(AccessPolicy accessPolicy) {
        List<EdcPermission> permissions = new ArrayList<>();

        if (accessPolicy.getAllowedCompanies() != null && !accessPolicy.getAllowedCompanies().isEmpty()) {
            for (String company : accessPolicy.getAllowedCompanies()) {
                EdcPermission permission = new EdcPermission();
                permission.setAction("USE");
                permission.setConstraint(createCompanyConstraint(company));
                permissions.add(permission);
            }
        } else {
            EdcPermission permission = new EdcPermission();
            permission.setAction("USE");
            permissions.add(permission);
        }

        if (accessPolicy.getUsagePurpose() != null && !accessPolicy.getUsagePurpose().isEmpty()) {
            EdcPermission purpose = new EdcPermission();
            purpose.setAction("USE");
            purpose.setConstraint(createPurposeConstraint(accessPolicy.getUsagePurpose()));
            permissions.add(purpose);
        }
        return permissions;
    }

    private EdcConstraint createCompanyConstraint(String company) {
        EdcConstraint constraint = new EdcConstraint();
        constraint.setLeftOperand("BusinessPartnerNumber");
        constraint.setOperator("eq");
        constraint.setRightOperand(company);
        return constraint;
    }

    private EdcConstraint createPurposeConstraint(String purpose) {
        EdcConstraint constraint = new EdcConstraint();
        constraint.setLeftOperand("Purpose");
        constraint.setOperator("eq");
        constraint.setRightOperand(purpose);
        return constraint;
    }

}
