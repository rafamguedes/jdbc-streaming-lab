package com.jdbc.streaming.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {

  private String itemName;
  private String itemDescription;
  private Integer itemQuantity;

  private BigDecimal itemBuyPrice;
  private BigDecimal itemSellPrice;

  private LocalDateTime itemCreatedAt;
  private LocalDateTime itemUpdatedAt;

  private String supplierName;
  private String supplierDescription;
  private String supplierCnpj;
  private String supplierEmail;
  private String supplierPhone;
}
