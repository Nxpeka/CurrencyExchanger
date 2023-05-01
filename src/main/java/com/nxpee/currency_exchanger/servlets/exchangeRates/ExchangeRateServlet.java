package com.nxpee.currency_exchanger.servlets.exchangeRates;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nxpee.currency_exchanger.dto.ExchangeRatesDTO;
import com.nxpee.currency_exchanger.exception.InvalidParametersException;
import com.nxpee.currency_exchanger.service.ExchangeRatesService;
import com.nxpee.currency_exchanger.util.ExceptionMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet(value = "/exchangeRate/*", name = "ExchangeRateServlet")
public class ExchangeRateServlet extends HttpServlet {

    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase("PATCH")){
            this.doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String requestPair = req.getPathInfo().replace("/", "").toUpperCase();
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            PrintWriter writer = resp.getWriter();
            Optional<ExchangeRatesDTO> exchangeRatePair = exchangeRatesService.findPair(requestPair);
            if (exchangeRatePair.isPresent()) {
                ExchangeRatesDTO exchangeRate = exchangeRatePair.get();
                String parameter = req.getReader().readLine().toLowerCase();

                ExchangeRatesDTO updatedRate = exchangeRatesService.updateRate(exchangeRate, parameter);

                String rate = objectMapper.writeValueAsString(updatedRate);
                writer.write(rate);
            } else {
                resp.setStatus(404);
                writer.write(objectMapper.writeValueAsString(new ExceptionMessage("Currency not found")));
            }

        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write(objectMapper.writeValueAsString(new ExceptionMessage(e.getMessage())));
        } catch (InvalidParametersException e) {
            resp.setStatus(400);
            resp.getWriter().write(objectMapper.writeValueAsString(new ExceptionMessage(e.getMessage())));
        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String requestPair = req.getPathInfo().replace("/", "").toUpperCase();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            PrintWriter writer = resp.getWriter();
            Optional<ExchangeRatesDTO> exchangeRatePair = exchangeRatesService.findPair(requestPair);
            if (exchangeRatePair.isPresent()) {
                writer.write(objectMapper.writeValueAsString(exchangeRatePair.get()));
            } else {
                resp.setStatus(404);
                writer.write(objectMapper.writeValueAsString(new ExceptionMessage("Currency not found")));
            }
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write(objectMapper.writeValueAsString(new ExceptionMessage(e.getMessage())));
        } catch (InvalidParametersException e) {
            resp.setStatus(400);
            resp.getWriter().write(objectMapper.writeValueAsString(new ExceptionMessage(e.getMessage())));
        }
    }
}
