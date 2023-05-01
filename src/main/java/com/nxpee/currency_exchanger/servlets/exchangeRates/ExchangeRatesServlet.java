package com.nxpee.currency_exchanger.servlets.exchangeRates;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nxpee.currency_exchanger.dto.ExchangeRatesDTO;
import com.nxpee.currency_exchanger.exception.AlreadyExistException;
import com.nxpee.currency_exchanger.exception.InvalidParametersException;
import com.nxpee.currency_exchanger.service.ExchangeRatesService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet(value = "/exchangeRates", name = "ExchangeRatesServlet")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            String exchangeRates = objectMapper.writeValueAsString(exchangeRatesService.findAll());
            PrintWriter writer = resp.getWriter();
            writer.write(exchangeRates);
        }catch (SQLException | InvalidParametersException e){
            resp.sendError(500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            ExchangeRatesDTO savedExchangeRates = exchangeRatesService.save(req.getParameterMap().entrySet());
            String exchangeRates = objectMapper.writeValueAsString(savedExchangeRates);
            PrintWriter writer = resp.getWriter();
            writer.write(exchangeRates);
        } catch (SQLException e) {
            resp.sendError(500, e.getMessage());
        } catch (InvalidParametersException e) {
            resp.sendError(400, e.getMessage());
        } catch (AlreadyExistException e) {
            resp.sendError(409, e.getMessage());
        }
    }
}
