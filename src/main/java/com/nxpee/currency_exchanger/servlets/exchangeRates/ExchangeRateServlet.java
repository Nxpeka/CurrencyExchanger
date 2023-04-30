package com.nxpee.currency_exchanger.servlets.exchangeRates;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nxpee.currency_exchanger.dto.ExchangeRatesDTO;
import com.nxpee.currency_exchanger.exception.InvalidParametersException;
import com.nxpee.currency_exchanger.service.ExchangeRatesService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String requestPair = req.getPathInfo().replace("/", "").toUpperCase();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PrintWriter writer = resp.getWriter();
            Optional<ExchangeRatesDTO> exchangeRatePair = exchangeRatesService.findPair(requestPair);
            if (exchangeRatePair.isPresent()) {
                writer.write(objectMapper.writeValueAsString(exchangeRatePair.get()));
            } else {
                resp.sendError(404, "ExchangeRate not found");
            }
        } catch (SQLException e) {
            resp.sendError(500, e.getMessage());
        } catch (InvalidParametersException e) {
            resp.sendError(400, e.getMessage());
        }
    }
}
