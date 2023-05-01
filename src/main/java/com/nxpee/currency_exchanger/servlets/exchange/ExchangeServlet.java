package com.nxpee.currency_exchanger.servlets.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nxpee.currency_exchanger.dto.response.ExchangeDTO;
import com.nxpee.currency_exchanger.exception.InvalidParametersException;
import com.nxpee.currency_exchanger.exception.NotFoundException;
import com.nxpee.currency_exchanger.service.ExchangeService;
import com.nxpee.currency_exchanger.dto.response.ExceptionMessageDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet(value = "/exchange", name = "ExchangeServlet")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeService exchangeService = ExchangeService.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            PrintWriter writer = resp.getWriter();
            ExchangeDTO exchangeDTO = exchangeService.calculate(req.getParameterMap().entrySet());
            writer.write(objectMapper.writeValueAsString(exchangeDTO));
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write(objectMapper.writeValueAsString(new ExceptionMessageDTO(e.getMessage())));
        } catch (InvalidParametersException e) {
            resp.setStatus(400);
            resp.getWriter().write(objectMapper.writeValueAsString(new ExceptionMessageDTO(e.getMessage())));
        } catch (NotFoundException e) {
            resp.setStatus(404);
            resp.getWriter().write(objectMapper.writeValueAsString(new ExceptionMessageDTO(e.getMessage())));
        }
    }
}
