package com.example.edwin.ayllu.ui;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.edwin.ayllu.R;
import com.example.edwin.ayllu.domain.Reporte;
import com.example.edwin.ayllu.io.AylluApiAdapter;
import com.example.edwin.ayllu.io.model.ReporteResponse;
import com.example.edwin.ayllu.ui.adapter.ReporteAdapter;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
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
    FloatingActionButton fab_report;
    ArrayList<Reporte> reportes = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ReporteAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_report, container, false);
        mReporteList = (RecyclerView) root.findViewById(R.id.reporte_list);
        setupReporteList();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        Call<ReporteResponse> call = AylluApiAdapter.getApiService().getReporte();
        call.enqueue(new Callback<ReporteResponse>() {
            @Override
            public void onResponse(Call<ReporteResponse> call, Response<ReporteResponse> response) {
                if(response.isSuccessful()) {
                    adapter.addAll(response.body().getReportes());
                    reportes = response.body().getReportes();
                }
            }

            @Override
            public void onFailure(Call<ReporteResponse> call, Throwable t) {
                Log.e("ERROR GRANDOTE",""+t.getMessage());
            }
        });

        fab_report = (FloatingActionButton) getActivity().findViewById(R.id.fab_report);
        fab_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!reportes.isEmpty()) {
                    Log.i("INFO","Generando reporte");
                    generateReport(reportes);
                }
            }
        });
    }

    public void generateReport(ArrayList<Reporte> rp){

        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        String format = s.format(new Date());
        s = new SimpleDateFormat("HH:mm:ss");
        format += "["+ s.format(new Date()) + "]";
        //----------------------------------------------------------------------------------
        //Escribiendo en el archivo Excel
        try {
            InputStream editor = getResources().openRawResource(R.raw.plantilla);
            FileOutputStream result = new FileOutputStream("/storage/sdcard0/Download/Qhapaq-Ñan"+format+".xls");

            //Crear el objeto que tendra el libro de Excel
            HSSFWorkbook workbook = new HSSFWorkbook(editor);
            HSSFWorkbook hssfWorkbookNew = new HSSFWorkbook();

            //1. Obtenemos la primera hoja del Excel
            //2. Obtenemos el iterator para recorrer las filas de la hoja
            HSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            recorrerExcel(1,12,rowIterator);

            workbook.write(result);
            result.close();
            workbook.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void recorrerExcel(int pi, int pf, Iterator<Row> rowIterator) {
        Row row;
        int con = 0;
        Boolean band = false;
        // Ciclo: Recorre cada una de las filas de la tabla Excel
        while(rowIterator.hasNext()){
            row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            Cell cell;
            String reporte = "1";
            if(con<reportes.size()) {
                reporte = "[" + reportes.get(con).getCod_paf() + "]" + "[" + reportes.get(con).getFecha_mon() + "]" +
                        "[" + reportes.get(con).getVariable() + "]" + "[" + reportes.get(con).getLatitud() + "]" +
                        "[" + reportes.get(con).getLongitud() + "]" + "[" + reportes.get(con).getRepercusiones() + "]" +
                        "[" + reportes.get(con).getOrigen() + "]";

                Log.e("Reporte",reporte);
            }

            //Ciclo: Recorre cada una de las celdas de la Fila
            while (cellIterator.hasNext()){
                cell = cellIterator.next();
                //Switch: Determinamos el tipo de dato de la celda
                switch (cell.getCellType()){
                    case Cell.CELL_TYPE_STRING:
                        if(cell.getStringCellValue().equals("Institucion:")){
                            Log.e("INFO","Encontro el Departamento: "+cell.getStringCellValue());
                            band = true;
                        }
                        break;
                    case Cell.CELL_TYPE_BLANK:
                        if(band == true){
                            Log.e("INFO","Escribiendo en la celda");
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            cell.setCellValue(reporte);
                        }
                        break;
                }
            }
            con++;
            if(con == reportes.size()-1) row = null;
        }
    }

    private void setupReporteList(){
        mReporteList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mReporteList.setAdapter(adapter);
    }
}
