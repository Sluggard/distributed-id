package com.geega.bsc.id.service.controller;

import com.geega.bsc.id.client.IdClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

/**
 * IdController
 *
 * @author Jun.An3
 * @date 2022/05/11
 */
@Validated
@RestController
@RequestMapping("/api/v1/id")
public class IdController {

    @Autowired
    private IdClient idClient;

    @GetMapping(value = "/one")
    public Long one() {
        return idClient.id();
    }

    @GetMapping(value = "/list")
    public List<Long> list(@RequestParam(value = "num")
                           @Max(value = 10, message = "最大10")
                           @Min(value = 1, message = "最小1") Integer num) {
        List<Long> ids = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            Long tmpId;
            if ((tmpId = idClient.id()) != null) {
                ids.add(tmpId);
            }
        }
        return ids;
    }

}
