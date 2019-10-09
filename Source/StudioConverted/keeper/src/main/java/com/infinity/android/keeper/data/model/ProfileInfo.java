/**
 * 
 */

package com.infinity.android.keeper.data.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

/**
 * @author joshiroh
 */
public final class ProfileInfo {
    private String userName;
    private String surname;
    private String phoneNumber;
    private String locationCountry;
    private String locationState;
    private String locationCity;
    private String userMailId;
    private String alternatePhoneNumber;
    private List<AdditionalInfo> securityQuestionsList;

    /**
     * Constructor
     * @param userName
     * @param surname
     * @param phoneNumber
     * @param locationCountry
     * @param locationState
     * @param locationCity
     * @param userMailId
     * @param alternatePhoneNumber
     */
    public ProfileInfo(final String userName, final String surname, final String phoneNumber, final String locationCountry, final String locationState, final String locationCity,
            final String userMailId, final String alternatePhone) {
        this.userName = userName;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.locationCity = locationCity;
        this.locationCountry = locationCountry;
        this.locationState = locationState;
        this.userMailId = userMailId;
        this.alternatePhoneNumber = alternatePhone;
    }

    public final void resetQuestions() {
        if(null != securityQuestionsList) {
            securityQuestionsList.clear();
        }
        securityQuestionsList = null;
    }
    /**
     * Add security questions
     * @param question
     */
    public final void addSecurityQuestionInfo(final AdditionalInfo question) {
        if(null == securityQuestionsList) {
            securityQuestionsList = new ArrayList<AdditionalInfo>();
        }
        securityQuestionsList.add(question);
    }

    /**
     * Get list of security questions
     * @return questionsList
     */
    public final List<AdditionalInfo> getSecurityQuestions() {
        return securityQuestionsList;
    }

    /**
     * @return the userName
     */
    public final String getUserName() {
        return userName;
    }

    /**
     * @return the surname
     */
    public final String getSurname() {
        return surname;
    }

    /**
     * Get UserName + Surname
     * @return fullName
     */
    public final String getFullUserName() {
        String fullName = userName;
        if(!Strings.isNullOrEmpty(fullName)) {
            fullName = fullName + " ";
        }
        if(!Strings.isNullOrEmpty(surname)) {
            fullName = (!Strings.isNullOrEmpty(fullName)) ? (fullName + surname) : surname;
        }
        return fullName;
    }

    /**
     * @return the phoneNumber
     */
    public final String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @return the locationCountry
     */
    public final String getLocationCountry() {
        return locationCountry;
    }

    /**
     * @return the locationState
     */
    public final String getLocationState() {
        return locationState;
    }

    /**
     * @return the locationCity
     */
    public final String getLocationCity() {
        return locationCity;
    }


    /**
     * Get complete string of user location.
     * @return location
     */
    public final String getUserLocationDetails() {
        StringBuilder builder = new StringBuilder();
        if(!Strings.isNullOrEmpty(locationCity)) {
            builder.append(locationCity);
        }

        if(!Strings.isNullOrEmpty(locationState)) {
            if(builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(locationState);
        }

        if(!Strings.isNullOrEmpty(locationCountry)) {
            if(builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(locationCountry);
        }
        return builder.toString();
    }

    /**
     * @return the userMailId
     */
    public final String getUserMailId() {
        return userMailId;
    }

    /**
     * @return the alternatePhoneNumber
     */
    public final String getAlternatePhoneNumber() {
        return alternatePhoneNumber;
    }
}
