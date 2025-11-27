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

        EdcPolicyDefinition definition = new EdcPolicyDefinition();
        definition.setId(policyId);

        EdcPolicy policy = new EdcPolicy();
        policy.setPermissions(createPermissions(accessPolicy));

        definition.setPolicy(policy);
        return definition;
    }


    private List<EdcPermission> createPermissions(AccessPolicy accessPolicy) {

        List<EdcPermission> list = new ArrayList<>();

        // Allowed companies
        if (accessPolicy.getAllowedCompanies() != null && !accessPolicy.getAllowedCompanies().isEmpty()) {
            for (String company : accessPolicy.getAllowedCompanies()) {
                list.add(buildPermission(createCompanyConstraint(company)));
            }
        } else {
            list.add(buildPermission(null));
        }

        // Usage purpose
        if (accessPolicy.getUsagePurpose() != null && !accessPolicy.getUsagePurpose().isEmpty()) {
            list.add(buildPermission(createPurposeConstraint(accessPolicy.getUsagePurpose())));
        }

        return list;
    }


    private EdcPermission buildPermission(EdcConstraint constraint) {

        EdcPermission permission = new EdcPermission();
        permission.setAction("use");
        permission.setConstraint(constraint);
        return permission;
    }


    private EdcConstraint createCompanyConstraint(String company) {
        EdcConstraint c = new EdcConstraint();
        c.setLeftOperand("BusinessPartnerNumber");
        c.setOperator("eq");
        c.setRightOperand(company);
        return c;
    }

    private EdcConstraint createPurposeConstraint(String purpose) {
        EdcConstraint c = new EdcConstraint();
        c.setLeftOperand("Purpose");
        c.setOperator("eq");
        c.setRightOperand(purpose);
        return c;
    }

}
