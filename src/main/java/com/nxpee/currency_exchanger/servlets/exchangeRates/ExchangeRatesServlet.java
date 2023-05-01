package com.nxpee.currency_exchanger.servlets.exchangeRates;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nxpee.currency_exchanger.dto.ExchangeRatesDTO;
import com.nxpee.currency_exchanger.exception.AlreadyExistException;
import com.nxpee.currency_exchanger.exception.InvalidParametersException;
import com.nxpee.currency_exchanger.exception.NotFoundException;
import com.nxpee.currency_exchanger.service.ExchangeRatesService;
import com.nxpee.currency_exchanger.dto.response.ExceptionMessageDTO;
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
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            String exchangeRates = objectMapper.writeValueAsString(exchangeRatesService.findAll());
            PrintWriter writer = resp.getWriter();
            writer.write(exchangeRates);
        }catch (SQLException | NotFoundException e){
            resp.setStatus(500);
            resp.getWriter().write(objectMapper.writeValueAsString(new ExceptionMessageDTO(e.getMessage())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            ExchangeRatesDTO savedExchangeRates = exchangeRatesService.save(req.getParameterMap().entrySet());
            String exchangeRates = objectMapper.writeValueAsString(savedExchangeRates);
            PrintWriter writer = resp.getWriter();
            writer.write(exchangeRates);
        } catch (SQLException | NotFoundException e) {
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
