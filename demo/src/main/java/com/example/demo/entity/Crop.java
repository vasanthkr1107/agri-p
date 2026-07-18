package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "crops")
public class Crop {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 128)
	private String name;

	/** Comma-separated or single value, e.g. "Loamy" or "Loamy, Clay" */
	@Column(name = "suitable_soil", length = 256)
	private String suitableSoil;

	@Column(length = 64)
	private String season;

	@Column(name = "cost_per_acre", precision = 14, scale = 2)
	private BigDecimal costPerAcre;

	/** Yield per acre (e.g. quintals or tons — keep units consistent with marketPrice) */
	@Column(name = "expected_yield", precision = 14, scale = 4)
	private BigDecimal expectedYield;

	/** Price per unit of yield */
	@Column(name = "market_price", precision = 14, scale = 2)
	private BigDecimal marketPrice;

	@Column(length = 512)
	private String pesticides;

	@Column(name = "duration_days")
	private Integer durationDays;

	public Crop() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSuitableSoil() {
		return suitableSoil;
	}

	public void setSuitableSoil(String suitableSoil) {
		this.suitableSoil = suitableSoil;
	}

	public String getSeason() {
		return season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public BigDecimal getCostPerAcre() {
		return costPerAcre;
	}

	public void setCostPerAcre(BigDecimal costPerAcre) {
		this.costPerAcre = costPerAcre;
	}

	public BigDecimal getExpectedYield() {
		return expectedYield;
	}

	public void setExpectedYield(BigDecimal expectedYield) {
		this.expectedYield = expectedYield;
	}

	public BigDecimal getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(BigDecimal marketPrice) {
		this.marketPrice = marketPrice;
	}

	public String getPesticides() {
		return pesticides;
	}

	public void setPesticides(String pesticides) {
		this.pesticides = pesticides;
	}

	public Integer getDurationDays() {
		return durationDays;
	}

	public void setDurationDays(Integer durationDays) {
		this.durationDays = durationDays;
	}
}
