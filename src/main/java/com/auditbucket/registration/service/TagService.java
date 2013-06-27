package com.auditbucket.registration.service;

import com.auditbucket.helper.SecurityHelper;
import com.auditbucket.registration.dao.TagDaoI;
import com.auditbucket.registration.model.ICompany;
import com.auditbucket.registration.model.ISystemUser;
import com.auditbucket.registration.model.ITag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: mike
 * Date: 26/06/13
 * Time: 12:53 PM
 */

@Service
public class TagService {
    @Autowired
    private SecurityHelper securityHelper;

    @Autowired
    private SystemUserService sysUserService;

    @Autowired
    private TagDaoI tagDao;

    @Transactional
    public ITag processTag(ITag input) {
        if (input == null)
            return input;

        // Check security access
        input.setCompany(getCompany());

        // Check exists
        ITag existingTag = tagDao.findOne(input.getName(), input.getCompany().getId());
        if (existingTag != null)
            return existingTag;

        // audit change
        return tagDao.save(input);
    }

    private ICompany getCompany() {
        String userName = securityHelper.getLoggedInUser();
        ISystemUser su = sysUserService.findByName(userName);

        if (su == null)
            throw new SecurityException("Not authorised");

        return su.getCompany();
    }

    public ITag findTag(String tagName) {
        ICompany company = getCompany();
        if (company == null)
            return null;
        return tagDao.findOne(tagName, company.getId());
    }
}
