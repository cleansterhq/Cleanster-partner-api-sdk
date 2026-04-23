package com.cleanster.xml.client;

import jakarta.xml.bind.*;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Utility for marshalling Java objects to XML strings and unmarshalling XML strings back
 * to Java objects.  All Cleanster model classes are JAXB-annotated, so any model
 * (Booking, Property, User, etc.) and the generic {@code XmlApiResponse<T>} wrapper
 * can be round-tripped through this converter.
 *
 * <pre>{@code
 * Booking b = new Booking();
 * b.setId(42);
 * b.setStatus("confirmed");
 * String xml = XmlConverter.toXml(b);
 * // <?xml version="1.0" encoding="UTF-8"?><booking><id>42</id><status>confirmed</status></booking>
 *
 * Booking restored = XmlConverter.fromXml(xml, Booking.class);
 * }</pre>
 */
public final class XmlConverter {

    private XmlConverter() {}

    /**
     * Marshal a JAXB-annotated object to an XML string.
     *
     * @param obj any object annotated with {@code @XmlRootElement}
     * @return formatted XML string
     * @throws CleansterXmlException if marshalling fails
     */
    public static String toXml(Object obj) {
        try {
            JAXBContext ctx = JAXBContext.newInstance(obj.getClass());
            Marshaller m    = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            StringWriter sw = new StringWriter();
            m.marshal(obj, sw);
            return sw.toString();
        } catch (JAXBException e) {
            throw new CleansterXmlException("Failed to marshal object to XML: " + e.getMessage(), e);
        }
    }

    /**
     * Unmarshal an XML string to a JAXB-annotated object.
     *
     * @param xml   the XML string
     * @param clazz the target class
     * @param <T>   the target type
     * @return the unmarshalled object
     * @throws CleansterXmlException if unmarshalling fails
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromXml(String xml, Class<T> clazz) {
        try {
            JAXBContext   ctx = JAXBContext.newInstance(clazz);
            Unmarshaller  u   = ctx.createUnmarshaller();
            return (T) u.unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            throw new CleansterXmlException("Failed to unmarshal XML to " + clazz.getSimpleName()
                    + ": " + e.getMessage(), e);
        }
    }

    /**
     * Check whether a string is valid, non-empty XML.
     *
     * @param xml the candidate string
     * @return true if the string starts with {@code <?xml} or {@code <}
     */
    public static boolean isXml(String xml) {
        if (xml == null) return false;
        String trimmed = xml.trim();
        return trimmed.startsWith("<?xml") || trimmed.startsWith("<");
    }
}
