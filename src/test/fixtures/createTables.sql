drop SCHEMA public CASCADE;
CREATE SCHEMA public;

create table if not exists locations (
  id serial not null,
  city character varying not null,
  state character varying null,
  street_address character varying null,
  zip_code character varying null,
  constraint locations_pkey primary key (id),
  constraint unique_locations unique (city, state, street_address)
);

create table if not exists groups (
  id serial not null,
  name character varying not null,
  url character varying not null,
  summary character varying null,
  constraint groups_pkey primary key (id),
  constraint unique_groups unique (url)
);

create table if not exists location_group_map (
  location_id integer null,
  group_id integer null,
  constraint unique_location_group_map unique (location_id, group_id),
  constraint location_group_map_group_id_fkey foreign KEY (group_id) references groups (id),
  constraint location_group_map_location_id_fkey foreign KEY (location_id) references locations (id)
);

create table if not exists game_stores (
  id serial not null,
  location_id integer null,
  name character varying not null,
  url character varying not null,
  constraint game_stores_pkey primary key (id),
  constraint game_stores_location_id_fkey foreign KEY (location_id) references locations (id)
);

create table if not exists game_restaurants (
  id serial not null,
  location_id integer not null,
  name character varying not null,
  url character varying not null,
  constraint game_restaurants_pkey primary key (id),
  constraint unique_game_restaurants unique (location_id, name),
  constraint game_restaurants_location_id_fkey foreign KEY (location_id) references locations (id)
);


create table if not exists events (
  id serial not null,
  location_id integer null,
  description character varying null,
  name character varying not null,
  url character varying not null,
  is_convention boolean null,
  constraint events_pkey primary key (id),
  constraint unique_events unique (name, url),
  constraint events_location_id_fkey foreign KEY (location_id) references locations (id)
);



DO $$ BEGIN
    create type dayOfWeek AS enum ('sunday', 'monday', 'tuesday', 'wednesday','thursday','friday', 'saturday');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;


create table if not exists event_time (
  id serial not null,
  event_id integer null,
  start_time timestamp with time zone null,
  end_time timestamp with time zone null,
  day_of_week dayofweek null,
  constraint event_time_pkey primary key (id),
  constraint event_time_event_id_fkey foreign KEY (event_id) references events (id)
);


DO $$ BEGIN
    create type event_tag as enum (
        'boardgame'
    );
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

create table if not exists event_tag_map (
  id serial not null,
  event_id integer null,
  tag event_tag null,
  constraint event_tag_map_pkey primary key (id),
  constraint unique_event_tag_map unique (event_id, tag),
  constraint event_tag_map_event_id_fkey foreign KEY (event_id) references locations (id)
);

create table if not exists event_group_map (
  group_id integer null,
  event_id integer null,
  constraint unique_event_group_map unique (group_id, event_id),
  constraint event_group_map_event_id_fkey foreign KEY (event_id) references events (id),
  constraint event_group_map_group_id_fkey foreign KEY (group_id) references groups (id)
);

