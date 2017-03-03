package com.example.edwin.ayllu.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.edwin.ayllu.R;
import com.example.edwin.ayllu.domain.Reporte;
import com.example.edwin.ayllu.io.AylluApiAdapter;
import com.example.edwin.ayllu.io.model.ReporteResponse;
import com.example.edwin.ayllu.ui.adapter.ReporteAdapter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportFragment extends Fragment {
    private ReporteAdapter adapter;
    private RecyclerView mReporteList;
    private BottomNavigationItemView btt_menu;
    private BottomNavigationView btt_men;

    FloatingActionButton fab_report;
    ArrayList<Reporte> reportes = new ArrayList<>();

    //==============================================================================================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ReporteAdapter(getActivity());
    }

    //==============================================================================================
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_report, container, false);
        mReporteList = (RecyclerView) root.findViewById(R.id.reporte_list);
        setupReporteList();
        return root;
    }

    //==============================================================================================
    @Override
    public void onResume() {
        super.onResume();

        Call<ReporteResponse> call = AylluApiAdapter.getApiService("REPORTE").getReporte(1,0,0,0);
        call.enqueue(new Callback<ReporteResponse>() {
            @Override
            public void onResponse(Call<ReporteResponse> call, Response<ReporteResponse> response) {
                if (response.isSuccessful()) {
                    adapter.addAll(response.body().getReportes());
                    reportes = response.body().getReportes();
                }
            }

            @Override
            public void onFailure(Call<ReporteResponse> call, Throwable t) {
            }
        });

        fab_report = (FloatingActionButton) getActivity().findViewById(R.id.fab_report);
        fab_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!reportes.isEmpty()) {
                    Log.i("INFO", "Generando reporte");
                    generateReport(reportes);
                }
            }
        });
    }

    //==============================================================================================
    public void generateReport(ArrayList<Reporte> rp) {

        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        String format = s.format(new Date());
        s = new SimpleDateFormat("HH:mm:ss");
        format += "[" + s.format(new Date()) + "]";
        //------------------------------------------------------------------------------------------
        //Escribiendo en el archivo Excel
        try {
            InputStream editor = getResources().openRawResource(R.raw.plantilla);
            FileOutputStream result = new FileOutputStream("/storage/sdcard0/Download/Qhapaq-Ñan" + format + ".xls");

            //Crear el objeto que tendra el libro de Excel
            HSSFWorkbook workbook = new HSSFWorkbook(editor);
            HSSFWorkbook hssfWorkbookNew = new HSSFWorkbook();

            //1. Obtenemos la primera hoja del Excel
            //2. Llenamos la primera hoja del Excel
            HSSFSheet sheet = workbook.getSheetAt(0);
            escribirExcel(1, 12, sheet);

            //1. Obtenemos la segunda hoja del Excel
            //2. Llenamos la segunda hoja del Excel
            sheet = workbook.getSheetAt(1);
            escribirExcel(2, 6, sheet);

            //1. Obtenemos la tercera hoja del Excel
            //2. Llenamos la tercera hoja del Excel
            sheet = workbook.getSheetAt(2);
            escribirExcel(3, 6, sheet);

            workbook.write(result);
            result.close();
            workbook.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //==============================================================================================
    private void escribirExcel(int cod_plan , int pf, HSSFSheet sheet) {
        //Estilo de celda basico
        CellStyle style = sheet.getWorkbook().createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.DASHED);
        style.setBorderTop(BorderStyle.DASHED);
        style.setBorderRight(BorderStyle.DASHED);
        style.setBorderLeft(BorderStyle.DASHED);

        //Estilo de celda para Repercusiones y Origenes
        CellStyle style2 = sheet.getWorkbook().createCellStyle();
        style2.setAlignment(HorizontalAlignment.CENTER);
        style2.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
        style2.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(BorderStyle.DASHED);
        style2.setBorderTop(BorderStyle.DASHED);
        style2.setBorderRight(BorderStyle.DASHED);
        style2.setBorderLeft(BorderStyle.DASHED);

        //Estilo de celda para Repercusiones y Origenes
        CellStyle style3 = sheet.getWorkbook().createCellStyle();
        style3.setAlignment(HorizontalAlignment.CENTER);
        style3.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
        style3.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style3.setBorderBottom(BorderStyle.DASHED);
        style3.setBorderTop(BorderStyle.DASHED);
        style3.setBorderRight(BorderStyle.DASHED);
        style3.setBorderLeft(BorderStyle.DASHED);

        //Variable para el punto de escritura
        int punto = pf;

        //------------------------------------------------------------------------------------------
        for (int i = 0; i < reportes.size(); i++) {
            HSSFRow fila = sheet.createRow(punto);
            HSSFCell celda;
            ArrayList<String> info = reportes.get(i).generarInfoPlantilla(cod_plan);
            punto++;
            //--------------------------------------------------------------------------------------
            for (int j = 0; j < info.size(); j++) {
                celda = fila.createCell(j);
                if (cod_plan == 1 && j > 6 && j < 11) {
                    if (info.get(j).equals("1")) celda.setCellStyle(style2);
                    else celda.setCellStyle(style);
                }
                else if (cod_plan == 1 && j > 10){
                    if (info.get(j).equals("1")) celda.setCellStyle(style3);
                    else celda.setCellStyle(style);
                }
                else {
                    celda.setCellValue(info.get(j));
                    celda.setCellStyle(style);
                }
            }
        }
    }

    //==============================================================================================
    private void recorrerExcel(int pi, int pf, int ph, Iterator<Row> rowIterator, HSSFSheet sht) {
        Row row;
        int i = 0, con = 0, j;
        Boolean band = false;
        ArrayList<String> info;

        // Ciclo: Recorre cada una de las filas de la tabla Excel
        while (rowIterator.hasNext() || i < reportes.size()) {
            if (!rowIterator.hasNext()) {
                HSSFRow rt = sht.createRow(con);
                HSSFCell cl = rt.createCell(1);
            }

            con++;
            row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            Cell cell;
            j = 0;
            band = false;

            //Ciclo: Recorre cada una de las celdas de la Fila
            while (cellIterator.hasNext()) {
                cell = cellIterator.next();
                //Switch: Determinamos el tipo de dato de la celda
            }
        }
    }

    //==============================================================================================
    private void setupReporteList() {
        mReporteList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mReporteList.setAdapter(adapter);
    }
}
