/**
 * Copyright (c) 2012
 * Fraunhofer Institute for Manufacturing Engineering
 * and Automation (IPA)
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the
 * distribution.
 * - Neither the name of the Fraunhofer Institute for Manufacturing
 * Engineering and Automation (IPA) nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * This program is free software: you can redistribute it and/or
 * modify
 * it under the terms of the GNU Lesser General Public License LGPL as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License LGPL for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License LGPL along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package de.fraunhofer.ipa;

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.Util;
import hudson.model.Descriptor.FormException;
import hudson.model.User;
import hudson.util.FormValidation;

import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.ServletException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

public class RootRepositoryProperty extends RepositoryProperty {

	private String suffix;
	
	protected String fullName;
	
	/**
	 * Repository dependencies
	 */
	//private volatile RepositoryList repoDeps = new RepositoryList();
	private final ArrayList<RepositoryProperty> repoDeps;
	
	@DataBoundConstructor
	public RootRepositoryProperty(String repoName, String fullName, String suffix, String fork, String branch, List<RepositoryProperty> repoDeps) {
		super(repoName, fork, branch);
		this.suffix = suffix;
		if (suffix.length() == 0) {
			this.fullName = repoName;
		} else {
			this.fullName = this.repoName+"__"+suffix;
		}
		this.repoDeps = new ArrayList<RepositoryProperty>(Util.fixNull(repoDeps));
	}
	
	public RootRepositoryProperty(String repoName, String fullName, String suffix, String fork, String branch, RepositoryProperty... repoDeps) {
		this(repoName, fullName, suffix, fork, branch, Arrays.asList(repoDeps));
	}
		
	@Override
	public void setRepoName(String repoName) {
		this.repoName = repoName;
	}
	
	public String getFullName() {
		return this.fullName;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	public String getSuffix() {
		return this.suffix;
	}
	
	/*public void setRepoDeps(RepositoryList repoDeps) throws IOException {
		this.repoDeps = new RepositoryList(repoDeps);
	}*/
	
	public List<RepositoryProperty> getRepoDeps() {
		return repoDeps;
	}
	
	@Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }
	
	@Extension
    public static class DescriptorImpl extends RepositoryPropertyDescriptor {
		@Override
        public String getDisplayName() {
            return "Root Repository Configurations";
        }
		
		@Override
		public boolean isRoot() {
			return true;
		}
		
		public FormValidation doCheckSuffix(@QueryParameter String value, @QueryParameter String repoName)
				throws IOException, ServletException {
			//TODO check if other repo with the same name exists
			
			if (value.length() != 0) { 
				return FormValidation.ok("Full name: "+repoName+"__"+value);
			} else {
				return FormValidation.ok("Full name: "+repoName);
			}
		}
        
        /**
         * All {@link RepositoryDescriptor}s
         */
        public List<RepositoryPropertyDescriptor> getRepositoryDescriptors() {
        	List<RepositoryPropertyDescriptor> r = new ArrayList<RepositoryPropertyDescriptor>();
        	for (RepositoryPropertyDescriptor d : RepositoryProperty.all()) {
        		// add only RepositoryDescriptors not RootRepositoryDescriptors
        		if (!d.isRoot())
        			r.add(d);
        	}
        	return r;
        }
	}

	@Override
    public RepositoryProperty reconfigure(StaplerRequest req, JSONObject form) throws FormException {
    	req.bindJSON(this, form);
    	return this;
    }
    
    /**
     * Returns all the registered {@link RepositoryPropertyDescriptor}s.
     */
    //return only Root
    public static DescriptorExtensionList<RepositoryProperty, RepositoryPropertyDescriptor> all() {
        return Jenkins.getInstance().<RepositoryProperty, RepositoryPropertyDescriptor>getDescriptorList(RepositoryProperty.class);
    }
}
