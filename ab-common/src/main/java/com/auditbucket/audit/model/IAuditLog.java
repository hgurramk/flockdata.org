/*
 * Copyright (c) 2012-2013 "Monowai Developments Limited"
 *
 * This file is part of AuditBucket.
 *
 * AuditBucket is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AuditBucket is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AuditBucket.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.auditbucket.audit.model;

import com.auditbucket.registration.model.IFortressUser;

import java.util.Date;
import java.util.Map;

/**
 * User: mike
 * Date: 15/04/13
 * Time: 5:49 AM
 */
public interface IAuditLog {

    String CREATE = "Create";
    String UPDATE = "Update";

    public abstract IAuditHeader getHeader();

    public abstract IFortressUser getWho();

    public abstract Date getWhen();

    /**
     * @return UTC time that this record was created
     */
    public abstract Date getSysWhen();

    public String getComment();

    /**
     * optional comment
     *
     * @param comment searchable.
     */
    public void setComment(String comment);

    /**
     * Document primary key as stored in search engine
     *
     * @param changeKey unique key
     */
    public void setSearchKey(String changeKey);

    /**
     * @return unique identifier to the search index key
     */
    public String getSearchKey();

    public String getJsonWhat();

    String getName();

    public void setTxRef(ITxRef txRef);

    String getEvent();

    Map<String, Object> getWhat();
}
