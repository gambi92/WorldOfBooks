--
-- PostgreSQL database dump
--

-- Dumped from database version 10.1
-- Dumped by pg_dump version 10.1

-- Started on 2018-02-24 20:35:02

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 1 (class 3079 OID 12924)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2808 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- TOC entry 585 (class 1247 OID 16648)
-- Name: OrderItemStatus; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE "OrderItemStatus" AS ENUM (
    'IN_STOCK',
    'OUT_OF_STOCK'
);


ALTER TYPE "OrderItemStatus" OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 196 (class 1259 OID 16653)
-- Name: order; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE "order" (
    orderid bigint NOT NULL,
    buyername text,
    buyeremail text,
    orderdate date,
    ordertotalvalue numeric,
    address text,
    postcode integer
);


ALTER TABLE "order" OWNER TO postgres;

--
-- TOC entry 197 (class 1259 OID 16661)
-- Name: order_item; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE order_item (
    orderitemid bigint NOT NULL,
    orderid bigint,
    saleprice numeric,
    shippingprice numeric,
    totalitemprice numeric,
    sku text,
    status "OrderItemStatus"
);


ALTER TABLE order_item OWNER TO postgres;

--
-- TOC entry 2680 (class 2606 OID 16668)
-- Name: order_item order_item_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY order_item
    ADD CONSTRAINT order_item_pkey PRIMARY KEY (orderitemid);


--
-- TOC entry 2678 (class 2606 OID 16660)
-- Name: order order_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "order"
    ADD CONSTRAINT order_pkey PRIMARY KEY (orderid);


-- Completed on 2018-02-24 20:35:04

--
-- PostgreSQL database dump complete
--

