package com.serh.trackmoney.controller;

import com.serh.trackmoney.dto.ConsumptionDto;
import com.serh.trackmoney.exception.api.CategoryNotFoundException;
import com.serh.trackmoney.exception.api.ConsumptionNotFoundException;
import com.serh.trackmoney.service.CategoryService;
import com.serh.trackmoney.service.ConsumptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.function.Supplier;

import static com.serh.trackmoney.dto.ConsumptionDto.of;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/v1/consumptions")
@RequiredArgsConstructor
public class ConsumptionController {

    private final ConsumptionService consumptionService;
    private final CategoryService categoryService;

    private Supplier<ConsumptionNotFoundException> consumptionNotFoundException
            = () -> new ConsumptionNotFoundException("Consumption not found.");
    private Supplier<CategoryNotFoundException> categoryNotFoundException
            = () -> new CategoryNotFoundException("Category not found.");

    private static final String DEFAULT_PAGE_SIZE = "10";

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<ConsumptionDto> all(@RequestParam(value = "page", defaultValue = "1") final int page,
                                    @RequestParam(value = "size",
                                            defaultValue = DEFAULT_PAGE_SIZE) final int size,
                                    @RequestParam(value = "sort",
                                            defaultValue = "id,asc") final String sort) {
        return consumptionService.findAll(page, size, sort).map(ConsumptionDto::of);
    }

    @GetMapping(value = "/category/{categoryId}")
    public List<ConsumptionDto> getByCategory(@PathVariable final Long categoryId) {
        return categoryService.findOneById(categoryId)
                .orElseThrow(categoryNotFoundException)
                .getConsumption().stream()
                .map(ConsumptionDto::of)
                .collect(toList());
    }

    @GetMapping(value = "/{id}")
    public ConsumptionDto getOne(@PathVariable final Long id) {
        return of(consumptionService.findOneById(id)
                .orElseThrow(consumptionNotFoundException));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final Long id) {
        consumptionService.delete(id);
    }
}
