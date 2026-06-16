package de.muenchen.raumreservierung.adapter.ldap;

@SuppressWarnings("PMD.DataClass")
public final class LdapAttributes {

    public static final String GIVEN_NAME = "GivenName";
    public static final String SN = "sn";
    public static final String TELEPHONE_NUMBER = "telephoneNumber";
    public static final String MAIL = "mail";

    public static final String LHM_OBJECT_ID = "lhmObjectID";
    public static final String ORGANISATIONAL_UNIT = "Department";

    private LdapAttributes() {
    }

}
