package com.jdbc.streaming.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {

  private String itemName;
  private String itemDescription;
  private Integer itemQuantity;

  private BigDecimal itemBuyPrice;
  private BigDecimal itemSellPrice;

  private String supplierName;
  private String supplierDescription;
  private String supplierCnpj;
  private String supplierEmail;
  private String supplierPhone;
}
