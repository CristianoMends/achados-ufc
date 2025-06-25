import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  //await app.listen(process.env.PORT ?? 3000);
  app.enableCors({
    origin: '*',
  });

  const port = process.env.PORT ? Number(process.env.PORT) : 3000;

  app.listen(port, '0.0.0.0', () => { 
    console.log(`Server listening on http://0.0.0.0:${port}`);
  });
}
bootstrap();
