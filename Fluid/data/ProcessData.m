function data = ProcessData( fileName, skipLines )
% Process data in file

data = zeros(40,10,6);
maxDisp = zeros(1,400);
wingLoad = zeros(1,400);
excentricity = zeros(1,400);


fid = fopen(fileName);

currentLine = 1;
while ~feof(fid)
  %skip lines at beginning
  if(currentLine<=skipLines)
    fgetl(fid);
  else
    s = fgetl(fid);
    r = regexp(s, ' ', 'split');
    r =str2double(r);
    i = int8(r(1)) + 1;
    j = int8(r(2)) + 1;
    data(i,j,:)=r(3:end);
    
    %generate other data values
    maxDisp(currentLine) = r(7);
    wingLoad(currentLine) = pi * r(4);
    excentricity(currentLine) = r(4) / r(3);
  end
  currentLine = currentLine + 1;

end
fclose(fid);

%Do something w
figure(1);

hist(data(:,:,5));

figure(2);

hist(maxDisp);

figure(3);

scatter(wingLoad, maxDisp);


figure(4);

scatter(excentricity, maxDisp);
end

