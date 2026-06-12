package de.muenchen.raumreservierung.adapter.ldap;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import org.springframework.ldap.core.AttributesMapper;

public class LdapPersonDtoMapper implements AttributesMapper<LdapPersonDto> {
    @Override
    public LdapPersonDto mapFromAttributes(Attributes attributes) throws NamingException {
        return new LdapPersonDto(
                getAttributeValue(attributes, LdapAttributes.GIVEN_NAME),
                getAttributeValue(attributes, LdapAttributes.SN),
                getAttributeValue(attributes, LdapAttributes.TELEPHONE_NUMBER),
                getAttributeValue(attributes, LdapAttributes.MAIL),
                getAttributeValue(attributes, LdapAttributes.LHM_OBJECT_ID),
                getAttributeValue(attributes, LdapAttributes.ORGANISATIONAL_UNIT));
    }

    private String getAttributeValue(Attributes attrs, String attributeName) throws NamingException {
        var attribute = attrs.get(attributeName);
        return attribute != null ? (String) attribute.get() : null;
    }
}
