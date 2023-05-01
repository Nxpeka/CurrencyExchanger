package com.nxpee.currency_exchanger.servlets.currencies;

import com.nxpee.currency_exchanger.dto.CurrenciesDTO;
import com.nxpee.currency_exchanger.exception.AlreadyExistException;
import com.nxpee.currency_exchanger.exception.InvalidParametersException;
import com.nxpee.currency_exchanger.service.CurrenciesService;
import com.nxpee.currency_exchanger.dto.response.ExceptionMessageDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet(value = "/currencies", name = "CurrenciesServlet")
public class CurrenciesServlet extends HttpServlet {

    private final CurrenciesService currenciesService = CurrenciesService.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            String currencies = objectMapper.writeValueAsString(currenciesService.findAll());
            PrintWriter writer = resp.getWriter();
            writer.write(currencies);
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write(objectMapper.writeValueAsString(new ExceptionMessageDTO(e.getMessage())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            CurrenciesDTO savedCurrencies = currenciesService.save(req.getParameterMap().entrySet());
            PrintWriter writer = resp.getWriter();
            writer.write(objectMapper.writeValueAsString(savedCurrencies));
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write(objectMapper.writeValueAsString(new ExceptionMessageDTO(e.getMessage())));
        } catch (InvalidParametersException e) {
            resp.setStatus(400);
            resp.getWriter().write(objectMapper.writeValueAsString(new ExceptionMessageDTO(e.getMessage())));
        } catch (AlreadyExistException e) {
            resp.setStatus(409);
            resp.getWriter().write(objectMapper.writeValueAsString(new ExceptionMessageDTO(e.getMessage())));
        }
    }
}