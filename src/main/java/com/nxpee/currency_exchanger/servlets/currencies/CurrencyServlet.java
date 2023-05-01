package com.nxpee.currency_exchanger.servlets.currencies;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nxpee.currency_exchanger.dto.CurrenciesDTO;
import com.nxpee.currency_exchanger.exception.InvalidParametersException;
import com.nxpee.currency_exchanger.exception.NotFoundException;
import com.nxpee.currency_exchanger.service.CurrenciesService;
import com.nxpee.currency_exchanger.dto.response.ExceptionMessageDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet(value = "/currency/*", name = "CurrencyServlet")
public class CurrencyServlet extends HttpServlet {

    private final CurrenciesService currenciesService = CurrenciesService.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String requestCode = req.getPathInfo().replace("/", "").toUpperCase();
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            PrintWriter writer = resp.getWriter();
            CurrenciesDTO currency = currenciesService.findByCode(requestCode);
            writer.write(objectMapper.writeValueAsString(currency));
        }catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write(objectMapper.writeValueAsString(new ExceptionMessageDTO(e.getMessage())));
        } catch (InvalidParametersException e) {
            resp.setStatus(400);
            resp.getWriter().write(objectMapper.writeValueAsString(new ExceptionMessageDTO(e.getMessage())));
        } catch (NotFoundException e) {
            resp.setStatus(404);
            resp.getWriter().write(objectMapper.writeValueAsString(new ExceptionMessageDTO("Currency not found")));
        }
    }
}
