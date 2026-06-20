CREATE TABLE IF NOT EXISTS toilets (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    address TEXT,
    lat DOUBLE PRECISION NOT NULL,
    lng DOUBLE PRECISION NOT NULL,
    agency VARCHAR(255),
    phone VARCHAR(100),
    hours VARCHAR(255),
    hours_detail TEXT,
    male_toilet INTEGER DEFAULT 0,
    male_urinal INTEGER DEFAULT 0,
    female_toilet INTEGER DEFAULT 0,
    disabled_male INTEGER DEFAULT 0,
    disabled_female INTEGER DEFAULT 0,
    emergency_bell BOOLEAN DEFAULT FALSE,
    diaper_table BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_toilet_location ON toilets (lat, lng);
CREATE INDEX IF NOT EXISTS idx_toilet_type ON toilets (type);

CREATE TABLE IF NOT EXISTS reviews (
    id BIGSERIAL PRIMARY KEY,
    toilet_id BIGINT NOT NULL REFERENCES toilets(id) ON DELETE CASCADE,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    photo_url TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_review_toilet ON reviews (toilet_id);
