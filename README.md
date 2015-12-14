# spreadsheet_simulator
Test description : Test OOP Generic.pdf
1. Project input and output data made via Standart input and output streams.
2. Unlimited input data size
3. Program uses temp files

Diffrence with previous version:
1. Unlimited input size, now input data stored in temp files(input data file and file with pointers(via RandomAcces) to input data temp file)
2. Power and bracket processing added ( cell example: =(A2^3+(22-7*5)^2)^5) )