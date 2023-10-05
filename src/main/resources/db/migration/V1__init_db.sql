CREATE TABLE location
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    incident_id UUID                     NOT NULL,
    coordinate  GEOGRAPHY(POINT, 4326),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL
);