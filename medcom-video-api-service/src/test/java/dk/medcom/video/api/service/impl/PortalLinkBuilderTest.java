package dk.medcom.video.api.service.impl;

import dk.medcom.video.api.dao.entity.GuestMicrophone;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import dk.medcom.video.api.service.PortalLinkBuilder;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PortalLinkBuilderTest {

    @Test
    void testParserAllValuesPresent() {
        var template = "http://citizen/?pin=__pin__&conference=__uri-with-domain__&microphone=__microphone__&start_dato=__start-date__&domain=__uri-domain__&redirectTo=__return-url__&callType=__call-type__&join=1#pin=__pin__&conference=__uri-with-domain__&microphone=__microphone__&start_dato=__start-date__&domain=__uri-domain__&redirectTo=__return-url__&callType=__call-type__&join=1";

        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.OCTOBER, 10, 9, 0, 0);
        var startTime = calendar.getTime();
        var meeting = new Meeting();
        meeting.setGuestMicrophone(GuestMicrophone.off);

        var schedulingInfo = new SchedulingInfo();
        schedulingInfo.setGuestPin(1234L);
        schedulingInfo.setHostPin(4321L);
        schedulingInfo.setUriWithDomain("asdf");
        schedulingInfo.setMeeting(meeting);
        schedulingInfo.setUriDomain("fdsa");
        schedulingInfo.setReturnUrl("qwer");
        schedulingInfo.setCallType("zxcv");

        var portalParser = new PortalLinkBuilder(template);

        var result = portalParser.buildPortalLink(startTime, schedulingInfo);
        assertEquals("http://citizen/?pin=1234&conference=asdf&microphone=off&start_dato=2019-10-10T09:00:00&domain=fdsa&redirectTo=qwer&callType=zxcv&join=1#pin=1234&conference=asdf&microphone=off&start_dato=2019-10-10T09:00:00&domain=fdsa&redirectTo=qwer&callType=zxcv&join=1", result);
    }

    @Test
    void testParserNoValuesPresent() {
        var template = "http://citizen/?pin=__pin__&conference=__uri-with-domain__&microphone=__microphone__&start_dato=__start-date__&domain=__uri-domain__&redirectTo=__return-url__&callType=__call-type__&join=1#pin=__pin__&conference=__uri-with-domain__&microphone=__microphone__&start_dato=__start-date__&domain=__uri-domain__&redirectTo=__return-url__&callType=__call-type__&join=1";

        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.OCTOBER, 10, 9, 0, 0);
        var startTime = calendar.getTime();

        var schedulingInfo = new SchedulingInfo();

        var portalParser = new PortalLinkBuilder(template);

        var result = portalParser.buildPortalLink(startTime, schedulingInfo);
        assertEquals("http://citizen/?pin=&start_dato=2019-10-10T09:00:00&join=1#pin=&conference=&microphone=&start_dato=2019-10-10T09:00:00&domain=&redirectTo=&callType=&join=1", result);
    }

    @Test
    void testParserMicrophoneOff() {
        var template = "http://citizen/?microphone=__microphone__#microphone=__microphone__";

        var startTime = Date.from(Instant.now());
        var meeting = new Meeting();
        meeting.setGuestMicrophone(GuestMicrophone.off);

        var schedulingInfo = new SchedulingInfo();
        schedulingInfo.setMeeting(meeting);

        var portalParser = new PortalLinkBuilder(template);

        var result = portalParser.buildPortalLink(startTime, schedulingInfo);
        assertEquals("http://citizen/?microphone=off#microphone=off", result);
    }

    @Test
    void testParserMicrophoneOn() {
        var template = "http://citizen/?microphone=__microphone__#microphone=__microphone__";

        var startTime = Date.from(Instant.now());
        var meeting = new Meeting();
        meeting.setGuestMicrophone(GuestMicrophone.on);

        var schedulingInfo = new SchedulingInfo();
        schedulingInfo.setMeeting(meeting);

        var portalParser = new PortalLinkBuilder(template);

        var result = portalParser.buildPortalLink(startTime, schedulingInfo);
        assertEquals("http://citizen/#microphone=", result);
    }

    @Test
    void testParserMicrophoneMuted() {
        var template = "http://citizen/?microphone=__microphone__#microphone=__microphone__";

        var startTime = Date.from(Instant.now());
        var meeting = new Meeting();
        meeting.setGuestMicrophone(GuestMicrophone.muted);

        var schedulingInfo = new SchedulingInfo();
        schedulingInfo.setMeeting(meeting);

        var portalParser = new PortalLinkBuilder(template);

        var result = portalParser.buildPortalLink(startTime, schedulingInfo);
        assertEquals("http://citizen/?microphone=muted#microphone=muted", result);
    }

    @Test
    void testParserMicrophoneNotSet() {
        var template = "http://citizen/?microphone=__microphone__#microphone=__microphone__";

        var startTime = Date.from(Instant.now());

        var schedulingInfo = new SchedulingInfo();

        var portalParser = new PortalLinkBuilder(template);

        var result = portalParser.buildPortalLink(startTime, schedulingInfo);
        assertEquals("http://citizen/#microphone=", result);
    }

    @Test
    void testParserGuestPin() {
        var template = "http://citizen/?pin=__pin__#pin=__pin__";

        var startTime = Date.from(Instant.now());

        var schedulingInfo = new SchedulingInfo();
        schedulingInfo.setGuestPin(1234L);
        schedulingInfo.setHostPin(4321L);

        var portalParser = new PortalLinkBuilder(template);

        var result = portalParser.buildPortalLink(startTime, schedulingInfo);
        assertEquals("http://citizen/?pin=1234#pin=1234", result);
    }

    @Test
    void testParserHostPin() {
        var template = "http://citizen/?pin=__pin__#pin=__pin__";

        var startTime = Date.from(Instant.now());

        var schedulingInfo = new SchedulingInfo();
        schedulingInfo.setGuestPin(null);
        schedulingInfo.setHostPin(4321L);

        var portalParser = new PortalLinkBuilder(template);

        var result = portalParser.buildPortalLink(startTime, schedulingInfo);
        assertEquals("http://citizen/?pin=4321#pin=4321", result);
    }

    @Test
    void testParserNoPin() {
        var template = "http://citizen/?pin=__pin__#pin=__pin__";

        var startTime = Date.from(Instant.now());

        var schedulingInfo = new SchedulingInfo();

        var portalParser = new PortalLinkBuilder(template);

        var result = portalParser.buildPortalLink(startTime, schedulingInfo);
        assertEquals("http://citizen/?pin=#pin=", result);
    }

    @Test
    void testParserNoTrailingSlash() {
        var template = "http://citizen?join=1#join=1";

        var startTime = Date.from(Instant.now());

        var schedulingInfo = new SchedulingInfo();

        var portalParser = new PortalLinkBuilder(template);

        var result = portalParser.buildPortalLink(startTime, schedulingInfo);
        assertEquals("http://citizen?join=1#join=1", result);
    }
}
