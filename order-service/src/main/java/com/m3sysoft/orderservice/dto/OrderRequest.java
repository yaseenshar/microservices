package com.m3sysoft.orderservice.dto;

import com.m3sysoft.orderservice.model.OrderLineItem;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    private List<OrderLineItemDto> orderLineItemListDto;
}
