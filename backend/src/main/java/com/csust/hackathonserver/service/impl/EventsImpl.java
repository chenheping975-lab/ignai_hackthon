package com.csust.hackathonserver.service.impl;

import com.csust.hackathonserver.Response.EventConfigRequest;
import com.csust.hackathonserver.Response.EventDetailVO;
import com.csust.hackathonserver.Response.FormFieldVO;
import com.csust.hackathonserver.Response.TrackVO;
import com.csust.hackathonserver.mapper.EventsMapper;
import com.csust.hackathonserver.mapper.FormFieldsMapper;
import com.csust.hackathonserver.mapper.TracksMapper;
import com.csust.hackathonserver.pojo.Events;
import com.csust.hackathonserver.pojo.FormFields;
import com.csust.hackathonserver.pojo.Tracks;
import com.csust.hackathonserver.service.EventsService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class EventsImpl implements EventsService {
    private static final String REGISTRATION_TARGET = "registration";
    private static final Set<String> FIELD_TYPES = Set.of("text", "textarea", "radio", "checkbox", "select", "date", "file", "url");

    @Autowired
    private EventsMapper eventsMapper;
    @Autowired
    private TracksMapper tracksMapper;
    @Autowired
    private FormFieldsMapper formFieldsMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Events getCurrentEvent() {
        return eventsMapper.getCurrentEvent();
    }

    @Override
    public Events getEventById(Long id) {
        return eventsMapper.findById(id);
    }

    @Override
    public EventDetailVO getCurrentEventDetail() {
        Events currentEvent = eventsMapper.getCurrentEvent();
        if (currentEvent == null) {
            return null;
        }
        return buildEventDetail(currentEvent);
    }

    @Override
    public EventDetailVO getEventDetail(Long id) {
        Events event = eventsMapper.findById(id);
        if (event == null) {
            return null;
        }
        return buildEventDetail(event);
    }

    @Override
    @Transactional
    public EventDetailVO saveEventConfig(Long id, EventConfigRequest request) {
        Events currentEvent = eventsMapper.findById(id);
        if (currentEvent == null || request == null) {
            return null;
        }

        Events event = new Events();
        event.setId(id);
        event.setTitle(valueOrDefault(request.getTitle(), currentEvent.getTitle()));
        event.setSubtitle(valueOrDefault(request.getSubtitle(), currentEvent.getSubtitle()));
        event.setLocation(valueOrDefault(request.getLocation(), currentEvent.getLocation()));
        event.setDescription(valueOrDefault(request.getDescription(), currentEvent.getDescription()));
        event.setStatus(valueOrDefault(request.getStatus(), stringify(currentEvent.getStatus())));
        event.setRegistrationOpen(booleanToInt(request.getRegistrationOpen(), currentEvent.getRegistrationOpen()));
        event.setRatingEnabled(booleanToInt(request.getRatingEnabled(), currentEvent.getRatingEnabled()));
        event.setVoteEnabled(booleanToInt(request.getVoteEnabled(), currentEvent.getVoteEnabled()));
        event.setRegistrationDeadline(request.getRegistrationDeadline() == null ? currentEvent.getRegistrationDeadline() : request.getRegistrationDeadline());
        event.setSubmissionDeadline(request.getSubmissionDeadline() == null ? currentEvent.getSubmissionDeadline() : request.getSubmissionDeadline());
        event.setRatingStartAt(request.getRatingStartAt() == null ? currentEvent.getRatingStartAt() : request.getRatingStartAt());
        event.setRatingEndAt(request.getRatingEndAt() == null ? currentEvent.getRatingEndAt() : request.getRatingEndAt());
        event.setOfficialSiteUrl(valueOrDefault(request.getOfficialSiteUrl(), currentEvent.getOfficialSiteUrl()));
        event.setBenchmarkSiteUrl(valueOrDefault(request.getBenchmarkSiteUrl(), currentEvent.getBenchmarkSiteUrl()));
        eventsMapper.updateEventConfig(event);

        replaceTracks(id, request.getTracks());
        replaceRegistrationFields(id, request.getRegistrationFields());

        return getEventDetail(id);
    }

    @Override
    public PageInfo<Events> listEvents(int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Events> list = eventsMapper.findAll();
        return new PageInfo<>(list);
    }

    private EventDetailVO buildEventDetail(Events event) {
        EventDetailVO vo = new EventDetailVO();
        vo.setId(event.getId());
        vo.setTitle(event.getTitle());
        vo.setSubtitle(event.getSubtitle());
        vo.setLocation(event.getLocation());
        vo.setDescription(event.getDescription());
        vo.setStatus(stringify(event.getStatus()));
        vo.setRegistrationOpen(Integer.valueOf(1).equals(event.getRegistrationOpen()));
        vo.setRatingEnabled(Integer.valueOf(1).equals(event.getRatingEnabled()));
        vo.setVoteEnabled(Integer.valueOf(1).equals(event.getVoteEnabled()));
        vo.setRegistrationDeadline(event.getRegistrationDeadline());
        vo.setSubmissionDeadline(event.getSubmissionDeadline());
        vo.setRatingStartAt(event.getRatingStartAt());
        vo.setRatingEndAt(event.getRatingEndAt());
        vo.setOfficialSiteUrl(event.getOfficialSiteUrl());
        vo.setBenchmarkSiteUrl(event.getBenchmarkSiteUrl());
        vo.setTracks(tracksMapper.findByEventId(event.getId()).stream().map(this::toTrackVO).toList());
        vo.setRegistrationFields(formFieldsMapper.findByEventIdAndTargetType(event.getId(), REGISTRATION_TARGET)
                .stream().map(this::toFormFieldVO).toList());
        return vo;
    }

    private void replaceTracks(Long eventId, List<TrackVO> tracks) {
        if (tracks == null) {
            return;
        }
        tracksMapper.deleteByEventId(eventId);
        int index = 1;
        for (TrackVO trackVO : tracks) {
            if (trackVO == null || isBlank(trackVO.getName())) {
                continue;
            }
            Tracks track = new Tracks();
            track.setEventId(eventId);
            track.setName(trackVO.getName().trim());
            track.setDescription(emptyToNull(trackVO.getDescription()));
            track.setSortOrder(trackVO.getSortOrder() == null ? index : trackVO.getSortOrder());
            tracksMapper.insertTrack(track);
            index++;
        }
    }

    private void replaceRegistrationFields(Long eventId, List<FormFieldVO> fields) {
        if (fields == null) {
            return;
        }
        formFieldsMapper.deleteByEventIdAndTargetType(eventId, REGISTRATION_TARGET);
        int index = 1;
        for (FormFieldVO fieldVO : fields) {
            if (fieldVO == null || isBlank(fieldVO.getLabel())) {
                continue;
            }
            FormFields field = new FormFields();
            field.setEventId(eventId);
            field.setTargetType(REGISTRATION_TARGET);
            field.setFieldKey(normalizeFieldKey(fieldVO.getFieldKey(), index));
            field.setLabel(fieldVO.getLabel().trim());
            field.setFieldType(normalizeFieldType(fieldVO.getFieldType()));
            field.setRequired(Boolean.TRUE.equals(fieldVO.getRequired()) ? 1 : 0);
            field.setOptionsJson(writeOptions(fieldVO.getOptions()));
            field.setPlaceholder(emptyToNull(fieldVO.getPlaceholder()));
            field.setSortOrder(fieldVO.getSortOrder() == null ? index : fieldVO.getSortOrder());
            field.setEnabled(fieldVO.getEnabled() == null || Boolean.TRUE.equals(fieldVO.getEnabled()) ? 1 : 0);
            formFieldsMapper.insertField(field);
            index++;
        }
    }

    private TrackVO toTrackVO(Tracks track) {
        TrackVO vo = new TrackVO();
        vo.setId(track.getId());
        vo.setEventId(track.getEventId());
        vo.setName(track.getName());
        vo.setDescription(track.getDescription());
        vo.setSortOrder(track.getSortOrder());
        return vo;
    }

    private FormFieldVO toFormFieldVO(FormFields field) {
        FormFieldVO vo = new FormFieldVO();
        vo.setId(field.getId());
        vo.setEventId(field.getEventId());
        vo.setTargetType(stringify(field.getTargetType()));
        vo.setFieldKey(field.getFieldKey());
        vo.setLabel(field.getLabel());
        vo.setFieldType(stringify(field.getFieldType()));
        vo.setRequired(Integer.valueOf(1).equals(field.getRequired()));
        vo.setOptions(readOptions(field.getOptionsJson()));
        vo.setPlaceholder(field.getPlaceholder());
        vo.setSortOrder(field.getSortOrder());
        vo.setEnabled(Integer.valueOf(1).equals(field.getEnabled()));
        return vo;
    }

    private String stringify(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private String valueOrDefault(String value, String defaultValue) {
        return value == null ? defaultValue : value;
    }

    private Integer booleanToInt(Boolean value, Integer defaultValue) {
        if (value == null) {
            return defaultValue == null ? 0 : defaultValue;
        }
        return value ? 1 : 0;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String emptyToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private String normalizeFieldKey(String fieldKey, int index) {
        if (isBlank(fieldKey)) {
            return "field_" + index;
        }
        String normalized = fieldKey.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
        return normalized.isEmpty() ? "field_" + index : normalized;
    }

    private String normalizeFieldType(String fieldType) {
        if (isBlank(fieldType)) {
            return "text";
        }
        String normalized = fieldType.trim().toLowerCase(Locale.ROOT);
        return FIELD_TYPES.contains(normalized) ? normalized : "text";
    }

    private String writeOptions(List<String> options) {
        try {
            List<String> safeOptions = options == null ? new ArrayList<>() : options.stream()
                    .filter(option -> !isBlank(option))
                    .map(String::trim)
                    .toList();
            return objectMapper.writeValueAsString(safeOptions);
        } catch (Exception ignored) {
            return "[]";
        }
    }

    private List<String> readOptions(Object optionsJson) {
        if (optionsJson == null) {
            return new ArrayList<>();
        }
        try {
            if (optionsJson instanceof List<?> list) {
                return list.stream().map(String::valueOf).toList();
            }
            return objectMapper.readValue(String.valueOf(optionsJson), new TypeReference<List<String>>() {});
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
    }
}
